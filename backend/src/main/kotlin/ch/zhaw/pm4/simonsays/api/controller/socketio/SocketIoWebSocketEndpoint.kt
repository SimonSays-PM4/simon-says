package ch.zhaw.pm4.simonsays.api.controller.socketio

import io.socket.engineio.server.EngineIoServer
import io.socket.engineio.server.EngineIoWebSocket
import io.socket.engineio.server.utils.ParseQS
import jakarta.websocket.*
import jakarta.websocket.server.ServerEndpoint
import org.springframework.stereotype.Component
import java.nio.ByteBuffer

/**
 * This class is a WebSocket endpoint for the Socket.IO server. It is used to handle WebSocket connections
 * and forward messages to the Socket.IO server.
 *
 * This class is adapted from https://socketio.github.io/engine.io-server-java/using.html#websocket-connections
 */
@Component
@ServerEndpoint("/socket.io/", configurator = WebSocketConfigurator::class)
@Suppress("unused")
class SocketIoWebSocketEndpoint(private val mEngineIoServer: EngineIoServer) {
    private lateinit var mSession: Session
    private lateinit var mQuery: Map<String, String>
    private lateinit var mEngineIoWebSocket: EngineIoWebSocket

    @OnOpen
    @Suppress("unused") // used automatically by the WebSocket API
    fun onOpen(session: Session) {
        mSession = session
        mQuery = ParseQS.decode(session.queryString)

        mEngineIoWebSocket = EngineIoWebSocketImpl()

        mSession.addMessageHandler(String::class.java) { message ->
            mEngineIoWebSocket.emit("message", message)
        }
        mSession.addMessageHandler(ByteArray::class.java) { message ->
            mEngineIoWebSocket.emit("message", message)
        }

        mEngineIoServer.handleWebSocket(mEngineIoWebSocket)
    }

    @OnClose
    @Suppress("unused") // used automatically by the WebSocket API
    fun onClose(session: Session, closeReason: CloseReason) {
        mEngineIoWebSocket.emit("close")
    }

    @OnError
    @Suppress("unused") // used automatically by the WebSocket API
    fun onError(session: Session, throwable: Throwable) {
        mEngineIoWebSocket.emit("error", "unknown error", throwable.message)
    }

    inner class EngineIoWebSocketImpl : EngineIoWebSocket() {
        private val mBasic: RemoteEndpoint.Basic = mSession.basicRemote

        override fun getQuery(): Map<String, String> {
            return mQuery
        }

        override fun getConnectionHeaders(): MutableMap<String, MutableList<String>> {
            return mutableMapOf() // not supported in websockets
        }

        override fun write(message: String) {
            mBasic.sendText(message)
        }

        override fun write(message: ByteArray) {
            mBasic.sendBinary(ByteBuffer.wrap(message))
        }

        override fun close() {
            mSession.close()
        }
    }
}