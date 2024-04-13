package ch.zhaw.pm4.simonsays.config

import ch.zhaw.pm4.simonsays.api.types.ErrorDTO
import ch.zhaw.pm4.simonsays.service.EventService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.*


@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class AdminEndpoint

@Component
@Order(1)
class AuthFilter(
    private val applicationProperties: ApplicationProperties,
    private val objectMapper: ObjectMapper,
    private val eventService: EventService,
    requestMappingHandlerMapping: RequestMappingHandlerMapping
) : OncePerRequestFilter() {

    private val matcher = AntPathMatcher()
    private val log = LoggerFactory.getLogger(javaClass)

    // Find all http mapped methods that are annotated with @AdminEndpoint in the entire application
    private final val adminEndpoints: Map<RequestMappingInfo, HandlerMethod> =
        requestMappingHandlerMapping.handlerMethods // Get all handler methods
            .filter { it.value.method.isAnnotationPresent(AdminEndpoint::class.java) } // Filter for methods annotated with @AdminEndpoint

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        log.debug("AuthFilter: ${request.method} ${request.requestURI}")

        val basicAuthToken = request.getHeader(HttpHeaders.AUTHORIZATION)

        // Check if the request has an Authorization header
        if (basicAuthToken == null || basicAuthToken.isBlank()) {
            return respondWithError(request, response, HttpStatus.UNAUTHORIZED, "No Authorization was provided")
        }

        // Check if auth
        if (!basicAuthToken.startsWith(BASIC_AUTH_PREFIX)) {
            return respondWithError(
                request,
                response,
                HttpStatus.BAD_REQUEST,
                "Invalid Authorization format. Make sure to start the token with '${BASIC_AUTH_PREFIX}'"
            )
        }

        // get credentials
        val (username, password) = try {
            extractCredentials(basicAuthToken)
        } catch (exception: Exception) {
            return respondWithError(
                request,
                response,
                HttpStatus.BAD_REQUEST,
                "Could not get credentials. Make sure to provide a valid basic authorization token"
            )
        }

        // Check if admin endpoint
        if (isAdminEndpoint(request)) {
            if (!isAdmin(username, password)) {
                return respondWithError(
                    request,
                    response,
                    HttpStatus.FORBIDDEN,
                    "Unauthorized access to admin endpoint"
                )
            }
            return filterChain.doFilter(request, response)
        }

        // Check if event endpoint
        if (isEventRelatedEndpoint(request)) {
            val eventId = getEventIdFromRequest(request) ?: return respondWithError(
                request, response, HttpStatus.BAD_REQUEST, "Invalid event id"
            )

            // Admins are allowed to access all events
            if (isAdmin(username, password)) {
                return filterChain.doFilter(request, response)
            }

            // Check if the username is allowed
            if (!isUsernameAllowed(username)) {
                return respondWithError(
                    request, response, HttpStatus.FORBIDDEN, "Username is not allowed (cannot be admin or empty)"
                )
            }

            // Check if the event credentials are valid
            if (!areEventCredentialsValid(eventId, password)) {
                return respondWithError(
                    request, response, HttpStatus.FORBIDDEN, "Unauthorized access to event or event does not exist"
                )
            }
            return filterChain.doFilter(request, response)
        }

        // By default, we allow all other request, for example for swagger and so
        return filterChain.doFilter(request, response)
    }

    /**
     * Check if the given request is targeting an admin endpoint
     */
    private fun isAdminEndpoint(request: HttpServletRequest): Boolean {
        val requestURI = request.requestURI
        val requestMethod = request.method
        for (entry in adminEndpoints) {
            val requestMappingInfo = entry.key
            // Check if the request method matches
            if (requestMappingInfo.methodsCondition.methods.none { it.name == requestMethod }) {
                continue
            }

            for (pattern in requestMappingInfo.patternValues) {
                if (matcher.match(pattern, requestURI)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Check if the given request is an event related endpoint
     */
    private fun isEventRelatedEndpoint(request: HttpServletRequest): Boolean {
        return matcher.match(EVENT_ANT_PATTERN, request.requestURI)
    }

    /**
     * Get the event id from an event related endpoint
     */
    private fun getEventIdFromRequest(request: HttpServletRequest): Long? {
        val match = matcher.extractUriTemplateVariables(EVENT_ANT_PATTERN, request.requestURI)
        return match["eventId"]?.toLongOrNull()
    }

    /**
     * Check if the given credentials are admin
     */
    private fun isAdmin(username: String, token: String): Boolean {
        return username == ADMIN_USERNAME && token == applicationProperties.adminToken
    }

    /**
     * Check if the given event credentials are valid.
     * @return true if the credentials are valid, false in case the event does not exist or the password is incorrect
     */
    private fun areEventCredentialsValid(eventId: Long, password: String): Boolean {
        try {
            val event = eventService.getEvent(eventId)
            return event.password == password
        } catch (exception: Exception) {
            return false
        }
    }

    /**
     * Check if the given username is allowed
     */
    private fun isUsernameAllowed(username: String): Boolean {
        // we do not allow empty usernames or the username "admin"
        val empty = username.trim().isEmpty()
        val isAdmin = username.lowercase() == ADMIN_USERNAME
        return !empty && !isAdmin
    }

    /**
     * Respond to the request with an error
     */
    private fun respondWithError(
        request: HttpServletRequest,
        response: HttpServletResponse,
        status: HttpStatus,
        message: String
    ) {
        val error = ErrorDTO(code = status.name, message = message)
        val errorJson = objectMapper.writeValueAsString(error)
        log.warn("${request.method} ${request.requestURI}: ${status.name} $message")
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer?.write(errorJson)
        response.status = status.value()
    }

    /**
     * Extracts the credentials from the Authorization header
     */
    private fun extractCredentials(basicAuthToken: String): Pair<String, String> {
        val encodedCredentials = basicAuthToken.substring(BASIC_AUTH_PREFIX.length)
        val decodedCredentials = String(Base64.getDecoder().decode(encodedCredentials))
        val parts = decodedCredentials.split(":", limit = 2)
        return Pair(parts[0], parts[1])
    }

    companion object {
        private const val BASIC_AUTH_PREFIX = "Basic "
        private const val ADMIN_USERNAME = "admin"
        private const val EVENT_ANT_PATTERN = "/rest-api/v1/event/{eventId}/**"
    }
}