package ch.zhaw.pm4.simonsays.api.controller.socketio

import ch.zhaw.pm4.simonsays.config.ApplicationProperties
import ch.zhaw.pm4.simonsays.config.auth.Access
import ch.zhaw.pm4.simonsays.config.auth.AuthService
import io.mockk.*
import io.socket.engineio.server.EngineIoServer
import io.socket.socketio.server.SocketIoServer
import io.socket.socketio.server.SocketIoSocket
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

class SocketIoTest {

    private lateinit var applicationProperties: ApplicationProperties
    private lateinit var authService: AuthService
    private lateinit var engineIoServer: EngineIoServer
    private lateinit var socketIoServer: SocketIoServer
    private lateinit var socketIoNamespaces: List<SocketIoNamespace<out Any, out Any>>
    private lateinit var socketIo: SocketIo

    @BeforeEach
    fun setup() {
        applicationProperties = mockk(relaxed = true)
        authService = mockk(relaxed = true)
        engineIoServer = mockk(relaxed = true)
        socketIoServer = mockk(relaxed = true)
        socketIoNamespaces = listOf()
        socketIo = SocketIo(applicationProperties, authService, socketIoNamespaces, engineIoServer, socketIoServer)
    }

    @Test
    fun `test service method with OPTIONS request`() {
        val req = mockk<HttpServletRequest>(relaxed = true)
        val resp = mockk<HttpServletResponse>(relaxed = true)

        every { req.method } returns HttpMethod.OPTIONS.name()

        socketIo.service(req, resp)

        verify { resp.status = 200 }
    }

    @Test
    fun `test service method with regular request`() {
        val req = mockk<HttpServletRequest>(relaxed = true)
        val resp = mockk<HttpServletResponse>(relaxed = true)
        val origin = "http://example.com"

        every { req.getHeader("Origin") } returns origin
        every { applicationProperties.frontendOrigins } returns arrayOf(origin)

        socketIo.service(req, resp)

        verify { resp.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin) }
        verify { resp.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true") }
        verify { resp.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,HEAD,PUT,PATCH,POST,DELETE") }
        verify { resp.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "origin,content-type,accept,Authorization") }
        verify { engineIoServer.handleRequest(req, resp) }
    }

    @Test
    fun `test service method with illegal origin regular request`() {
        val req = mockk<HttpServletRequest>(relaxed = true)
        val resp = mockk<HttpServletResponse>(relaxed = true)
        val origin = "http://example.com"

        every { req.getHeader("Origin") } returns origin
        every { applicationProperties.frontendOrigins } returns arrayOf("http://another.com")

        socketIo.service(req, resp)

        verify { engineIoServer.handleRequest(req, resp) }
    }

    @Test
    fun `test disconnectBecauseOfIllegalAccess method`() {
        val socket = mockk<SocketIoSocket>(relaxed = true)
        val access = Access.FORBIDDEN_EVENT

        socketIo.disconnectBecauseOfIllegalAccess(socket, access)

        verify {
            socket.send(
                SocketIoNamespace.APPLICATION_ERROR_EVENT,
                any()
            )
        }
        verify { socket.disconnect(any()) }
    }

    @Test
    fun `onSocketConnection with valid access should call onConnection`() {
        val socket = mockk<SocketIoSocket>(relaxed = true)
        val namespace = mockk<SocketIoNamespace<Any, Any>>(relaxed = true)
        val access = Access.ALLOWED

        every { authService.checkRequestAccess(uri = any(), authorizationHeaderValue =  any()) } returns access

        socketIo.onSocketConnection(socket, namespace)

        verify { namespace.onConnection(socket) }
    }

    @Test
    fun `onSocketConnection with invalid access should disconnect`() {
        val socket = mockk<SocketIoSocket>(relaxed = true)
        val namespace = mockk<SocketIoNamespace<Any, Any>>(relaxed = true)
        val access = Access.FORBIDDEN_EVENT

        every { authService.checkRequestAccess(uri = any(), authorizationHeaderValue =  any()) } returns access

        socketIo.onSocketConnection(socket, namespace)

        verify { socket.send(SocketIoNamespace.APPLICATION_ERROR_EVENT, any()) }
    }
}
