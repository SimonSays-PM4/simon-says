package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.config.PrinterProperties
import io.socket.engineio.server.EngineIoServer
import io.socket.engineio.server.EngineIoServerOptions
import io.socket.socketio.server.SocketIoServer
import io.socket.socketio.server.SocketIoSocket
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse


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
interface SocketIoNamespace {
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
    fun onDisconnection(socket: SocketIoSocket)
}

@WebServlet("/socket.io/*", asyncSupported = true)
class SocketIo(printerProperties: PrinterProperties, socketIoNamespaces: List<SocketIoNamespace>) : HttpServlet() {
    /**
     * Define the underlying Engine.IO server.
     */
    private val engineIoServer = EngineIoServer(
        EngineIoServerOptions.newFromDefault().setAllowedCorsOrigins(printerProperties.frontendOrigins)
            .setCorsHandlingDisabled(false)
    )

    /**
     * Define the Socket.IO server.
     */
    private val socketIoServer = SocketIoServer(engineIoServer)

    /**
     * Handle the incoming request and pass it to the Engine.IO server to handle it.
     */
    override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
        engineIoServer.handleRequest(req, resp)
    }

    init {
        // Register all namespaces.
        for (namespace in socketIoNamespaces) {
            socketIoServer.namespace {
                namespace.isPartOfNamespace(it)
            }.on("connection") { args ->
                val socket = args[0] as SocketIoSocket
                val origin = socket.client.initialHeaders["origin"]?.get(0) ?: "unknown"
                val namespaceName = socket.namespace.name
                val serverSocketId = socket.id
                val clientSocketId = socket.client.id
                log("client socket '$clientSocketId' connected to server socket '$serverSocketId' with namespace '$namespaceName' from origin '$origin'")
                namespace.onConnection(socket)
                socket.on("disconnect") {
                    log("client socket '$clientSocketId' disconnected from server socket '$serverSocketId' with namespace '$namespaceName'")
                    namespace.onDisconnection(socket)
                }

            }
        }
    }
}