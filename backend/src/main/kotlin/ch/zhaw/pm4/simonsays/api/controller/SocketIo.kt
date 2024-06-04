package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.types.printer.ApplicationErrorDto
import ch.zhaw.pm4.simonsays.config.ApplicationProperties
import ch.zhaw.pm4.simonsays.config.auth.Access
import ch.zhaw.pm4.simonsays.config.auth.AuthService
import ch.zhaw.pm4.simonsays.utils.sendPojo
import io.socket.engineio.server.EngineIoServer
import io.socket.engineio.server.EngineIoServerOptions
import io.socket.socketio.server.SocketIoServer
import io.socket.socketio.server.SocketIoSocket
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

/**
 * A namespace in socket.io is a way to separate the events that are handled by the server.
 * This is useful to separate different parts of the application and to avoid conflicts between events.
 *
 * **Every Bean that wants to handle namespace events needs to implement this interface.**
 *
 * In our case we separate the events very similar to the REST API where for example instead of doing
 *
 * GET rest-api/v1/printer-server/{id}/print-queues
 *
 * we can connect to
 *
 * socket-api/v1/printer-server/{id}/print-queues
 *
 * which would make "socket-api/v1/printer-server/{id}/print-queues" the namespace.
 */
interface SocketIoNamespace<K, T> {
    companion object {
        /** The change event name. */
        const val CHANGE_EVENT = "change"

        /** The initial data event name. */
        const val INITIAL_DATA_EVENT = "initial-data"

        /** The remove event name. */
        const val REMOVE_EVENT = "remove"

        /** The application error event name. */
        const val APPLICATION_ERROR_EVENT = "application-error"
    }

    /**
     * This method is called by the Socket.IO server to check if the given requested namespace name is part of the
     * namespace handled by this bean.
     * @param requestedNamespace The requested namespace name.
     * @return True if the requested namespace is part of the namespace handled by this bean, false otherwise.
     */
    fun isPartOfNamespace(requestedNamespace: String): Boolean

    /**
     * This method is called by the Socket.IO server to handle the connection event for the given namespace.
     * @param socket The socket that represents the connection.
     */
    fun onConnection(socket: SocketIoSocket)

    /**
     * This method is called by the Socket.IO server to handle the disconnection event for the given namespace.
     * @param socket The socket that represents the connection.
     */
    fun onDisconnect(socket: SocketIoSocket)

    /**
     * This method may be called when an object changes and needs to be sent to all connected clients.
     * @param data The data to send to all connected clients.
     */
    fun onChange(data: T)

    /**
     * This method is called when an object is removed and needs to be removed from all connected clients.
     * @param data The data to remove from all connected clients.
     */
    fun onRemove(data: T)

    /**
     * This method is called when an application error occurs and needs to be sent to all connected clients.
     *
     * @param id The unique identifier to send to (printer server id, job id, ...). When null, the error will be sent to all connected clients. Otherwise, only subscribers of the given id will receive the error.
     * @param error The error that occurred.
     */
    fun onApplicationError(id: K?, error: ApplicationErrorDto)

    /**
     * Shorthand method for onApplicationError(id, ApplicationErrorDto(code, message)).
     */
    fun onApplicationError(id: K?, code: String, message: String) {
        val error = ApplicationErrorDto(code, message)
        onApplicationError(id, error)
    }

    /**
     * Send and error to specific socket
     */
    fun onApplicationError(socket: SocketIoSocket, error: ApplicationErrorDto) {
        socket.sendPojo(APPLICATION_ERROR_EVENT, error)
    }

    /**
     * Shorthand method for onApplicationError(socket, ApplicationErrorDto(code, message))
     */
    fun onApplicationError(socket: SocketIoSocket, code: String, message: String) {
        val error = ApplicationErrorDto(code, message)
        onApplicationError(socket, error)
    }
}

@WebServlet("/socket.io/*", asyncSupported = true)
class SocketIo(
    private val applicationProperties: ApplicationProperties,
    private val authService: AuthService,
    socketIoNamespaces: List<SocketIoNamespace<out Any, out Any>>,
    private val engineIoServer: EngineIoServer = EngineIoServer(EngineIoServerOptions.newFromDefault().apply {
        setCorsHandlingDisabled(true)
    }),
    socketIoServer: SocketIoServer = SocketIoServer(engineIoServer),
) : HttpServlet() {

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        // Register all namespaces.
        for (namespace in socketIoNamespaces) {
            socketIoServer.namespace {
                namespace.isPartOfNamespace(it)
            }.on("connection") { args ->
                val socket = args[0] as SocketIoSocket
                onSocketConnection(socket, namespace)
            }
        }
    }

    /**
     * Handle the incoming request and pass it to the Engine.IO server to handle it.
     */
    override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
        // configure cors
        if (applicationProperties.frontendOrigins.contains(req.getHeader("Origin"))) {
            resp.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, req.getHeader("Origin"))
        }
        resp.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
        resp.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,HEAD,PUT,PATCH,POST,DELETE")
        resp.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "origin,content-type,accept,Authorization")
        // We need to allow OPTIONS calls for authorization
        if (req.method == HttpMethod.OPTIONS.name()) {
            resp.status = 200
            return
        }
        engineIoServer.handleRequest(req, resp)
    }

    /**
     * Invoked when a new socket connection is established.
     */
    fun onSocketConnection(
        socket: SocketIoSocket,
        namespace: SocketIoNamespace<out Any, out Any>
    ) {
        val origin = socket.client.initialHeaders["origin"]?.get(0) ?: "unknown"
        val namespaceName = socket.namespace.name
        val serverSocketId = socket.id
        val clientSocketId = socket.client.id
        val authorizationHeader = socket.initialHeaders["authorization"]?.get(0)
        val access = authService.checkRequestAccess(
            uri = namespaceName, authorizationHeaderValue = authorizationHeader
        )
        if (!access.allowed) {
            disconnectBecauseOfIllegalAccess(socket, access)
            return
        }

        log.info("client socket '$clientSocketId' connected to server socket '$serverSocketId' with namespace '$namespaceName' from origin '$origin'")
        namespace.onConnection(socket)
        socket.on("disconnect") {
            log.info("client socket '$clientSocketId' disconnected from server socket '$serverSocketId' with namespace '$namespaceName'")
            namespace.onDisconnect(socket)
        }
    }

    fun disconnectBecauseOfIllegalAccess(socket: SocketIoSocket, access: Access) {
        val disconnectMessage = "Client disconnected because of illegal access: ${access.name} (${access.message})"
        log.warn(disconnectMessage)
        socket.sendPojo(
            SocketIoNamespace.APPLICATION_ERROR_EVENT, ApplicationErrorDto(access.name, disconnectMessage)
        )
        socket.disconnect(true)
    }
}