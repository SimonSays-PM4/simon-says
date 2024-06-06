package ch.zhaw.pm4.simonsays.api.controller.socketio

import ch.zhaw.pm4.simonsays.api.types.printer.ApplicationErrorDTO
import ch.zhaw.pm4.simonsays.config.ApplicationProperties
import ch.zhaw.pm4.simonsays.config.auth.Access
import ch.zhaw.pm4.simonsays.config.auth.AuthService
import ch.zhaw.pm4.simonsays.utils.sendPojo
import io.socket.engineio.server.EngineIoServer
import io.socket.socketio.server.SocketIoServer
import io.socket.socketio.server.SocketIoSocket
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

@WebServlet("/socket.io/*", asyncSupported = true)
class SocketIo(
    private val applicationProperties: ApplicationProperties,
    private val authService: AuthService,
    socketIoNamespaces: List<SocketIoNamespace<out Any, out Any>>,
    private val engineIoServer: EngineIoServer,
    socketIoServer: SocketIoServer,
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
            SocketIoNamespace.APPLICATION_ERROR_EVENT, ApplicationErrorDTO(access.name, disconnectMessage)
        )
        socket.disconnect(true)
    }
}