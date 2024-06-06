package ch.zhaw.pm4.simonsays.api.controller.socketio

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.socket.engineio.server.EngineIoServer
import jakarta.websocket.CloseReason
import jakarta.websocket.MessageHandler
import jakarta.websocket.Session
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

class SocketIoWebSocketEndpointTest {
    private lateinit var endpoint: SocketIoWebSocketEndpoint
    private lateinit var session: Session
    private lateinit var engineIoServer: EngineIoServer

    @BeforeEach
    fun setup() {
        session = mockk(relaxed = true)
        engineIoServer = mockk(relaxed = true)
        endpoint = SocketIoWebSocketEndpoint(engineIoServer)
    }

    @Test
    @DisplayName("Should handle WebSocket on open")
    fun shouldHandleWebSocketOnOpen() {
        every { session.queryString } returns "test=query"
        endpoint.onOpen(session)
        verify { session.addMessageHandler(any<Class<String>>(), any<MessageHandler.Whole<String>>()) }
        verify { session.addMessageHandler(any<Class<ByteArray>>(), any<MessageHandler.Whole<ByteArray>>()) }
        verify { engineIoServer.handleWebSocket(any()) }
    }

    @Test
    @DisplayName("onClose should not throw an exception")
    fun shouldEmitCloseOnClose() {
        endpoint.onOpen(session)
        endpoint.onClose(session, CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, ""))
    }

    @Test
    @DisplayName("onError should not throw an exception")
    fun shouldEmitErrorOnError() {
        endpoint.onOpen(session)
        endpoint.onError(session, Throwable("Test error"))
    }

    @Test
    @DisplayName("Should write message when write method is called with string")
    fun shouldWriteMessageWithString() {
        val message = "Test message"
        endpoint.onOpen(session)
        endpoint.EngineIoWebSocketImpl().write(message)
        verify { session.basicRemote.sendText(message) }
    }

    @Test
    @DisplayName("Should write message when write method is called with byte array")
    fun shouldWriteMessageWithByteArray() {
        val message = "Test message".toByteArray()
        endpoint.onOpen(session)
        endpoint.EngineIoWebSocketImpl().write(message)
        verify { session.basicRemote.sendBinary(ByteBuffer.wrap(message)) }
    }

    @Test
    @DisplayName("Should close session when close method is called")
    fun shouldCloseSession() {
        endpoint.onOpen(session)
        endpoint.EngineIoWebSocketImpl().close()
        verify { session.close() }
    }

    @Test
    @DisplayName("Should return query when getQuery method is called")
    fun shouldReturnQuery() {
        every { session.queryString } returns "test=query"
        endpoint.onOpen(session)
        val query = endpoint.EngineIoWebSocketImpl().getQuery()
        assertEquals(mapOf("test" to "query"), query)
    }

    @Test
    @DisplayName("Should return empty map when getConnectionHeaders method is called")
    fun shouldReturnEmptyMap() {
        endpoint.onOpen(session)
        val headers = endpoint.EngineIoWebSocketImpl().getConnectionHeaders()
        assertTrue(headers.isEmpty())
    }
}