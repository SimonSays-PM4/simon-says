package ch.zhaw.pm4.simonsays.config

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.service.EventService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.Base64

/**
 * Test class for the [AuthFilter].
 * Tests if the filter correctly allows or denies access to certain endpoints based on the provided credentials.
 */
class AuthFilterTest {
    private lateinit var authFilter: AuthFilter
    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var filterChain: FilterChain
    private lateinit var eventService: EventService

    private val adminToken = "mysecretpassword"
    private val incorrectAdminToken = "incorrectpassword"
    private val eventToken = "myeventtoken"
    private val incorrectEventToken = "incorrecteventtoken"
    private val adminUsername = "admin"

    @BeforeEach
    fun setup() {
        val mockApplicationProperties: ApplicationProperties = mockk(relaxed = true)
        eventService = mockk(relaxed = true)
        val requestMappingHandlerMapping: RequestMappingHandlerMapping = mockk(relaxed = true)
        val objectMapper = jacksonObjectMapper()

        setupRequestMappingHandlerMapping(requestMappingHandlerMapping)
        every { mockApplicationProperties.adminToken } returns adminToken

        authFilter = AuthFilter(mockApplicationProperties, objectMapper, eventService, requestMappingHandlerMapping)
        request = mockk(relaxed = true)
        response = mockk(relaxed = true)
        filterChain = mockk(relaxed = true)
    }

    private fun setupRequestMappingHandlerMapping(mapping: RequestMappingHandlerMapping) {
        val requestMappingInfo: RequestMappingInfo = mockk(relaxed = true)
        val handlerMethod: HandlerMethod = mockk(relaxed = true)

        every { mapping.handlerMethods } returns mapOf(requestMappingInfo to handlerMethod)
        every { handlerMethod.method.isAnnotationPresent(AdminEndpoint::class.java) } returns true
        every { requestMappingInfo.patternValues } returns setOf(SAMPLE_ADMIN_ENDPOINT)
        every { requestMappingInfo.methodsCondition.methods } returns setOf(RequestMethod.GET)
    }

    @Test
    fun `Admin endpoint should allow access with valid credentials`() {
        setupRequest("Basic ${getBasicAuthToken(adminUsername, adminToken)}", "GET", "/rest-api/v1/event")
        authFilter.doFilterInternal(request, response, filterChain)
        verify(exactly = 1) { filterChain.doFilter(request, response) }
    }

    @Test
    fun `Admin endpoint should deny access with invalid credentials`() {
        setupRequest("Basic ${getBasicAuthToken(adminUsername, incorrectAdminToken)}", "GET", "/rest-api/v1/event")
        authFilter.doFilterInternal(request, response, filterChain)
        verify(exactly = 0) { filterChain.doFilter(request, response) }
    }

    @Test
    fun `API health endpoint should be accessible without credentials`() {
        setupRequest(null, "GET", "/api/health")
        authFilter.doFilterInternal(request, response, filterChain)
        verify(exactly = 1) { filterChain.doFilter(request, response) }
    }

    // Tests for event-related endpoints
    private fun testEventEndpointAccess(eventId: Long, password: String, shouldAccessBeAllowed: Boolean) {
        val mockEventDTO = createMockEventDTO(eventId, "SimonSaysBurger", eventToken, 10)
        every { eventService.getEvent(eventId) } returns mockEventDTO
        setupRequest("Basic ${getBasicAuthToken("user", password)}", "GET", "/rest-api/v1/event/$eventId")

        authFilter.doFilterInternal(request, response, filterChain)
        verify(exactly = if (shouldAccessBeAllowed) 1 else 0) { filterChain.doFilter(request, response) }
    }

    @Test
    fun `Event related endpoint should allow access with valid event credentials`() {
        testEventEndpointAccess(1L, eventToken, true)
    }

    @Test
    fun `Event related endpoint should deny access with invalid event credentials`() {
        testEventEndpointAccess(1L, incorrectEventToken, false)
    }

    private fun createMockEventDTO(id: Long, name: String, password: String, numberOfTables: Long): EventDTO {
        return EventDTO(name = name, password = password, numberOfTables = numberOfTables, id = id)
    }

    private fun setupRequest(authHeader: String?, method: String, uri: String) {
        every { request.getHeader(HttpHeaders.AUTHORIZATION) } returns authHeader
        every { request.method } returns method
        every { request.requestURI } returns uri
    }

    private fun getBasicAuthToken(username: String, password: String): String =
        Base64.getEncoder().encodeToString("$username:$password".toByteArray())

    companion object {
        private const val SAMPLE_ADMIN_ENDPOINT = "/rest-api/v1/event"
    }
}
