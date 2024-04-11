package ch.zhaw.pm4.simonsays.config

import ch.zhaw.pm4.simonsays.service.EventService
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.Base64

class EventAuthFilter(
    private val applicationProperties: ApplicationProperties,
    private val eventService: EventService
) : Filter {

    // Check if the request is authorized
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val basicAuthToken = httpRequest.getHeader(AUTHORIZATION_HEADER)
        val credentials = extractCredentials(basicAuthToken)

        if (credentials == null) {
            respondWithStatus(httpResponse, UNAUTHORIZED_MESSAGE, UNAUTHORIZED_STATUS)
            return
        }

        // Check if the credentials are valid
        if (isAdmin(credentials)) {
            continueChain(chain, request, response)
        } else {
            handleEventAuthentication(credentials, httpRequest, httpResponse, chain)
        }
    }

    // Extract the credentials from the Authorization header
    private fun extractCredentials(authHeader: String?): Pair<String, String>? {
        if (authHeader == null || !authHeader.startsWith(BASIC_PREFIX)) return null
        return try {
            val encodedCredentials = authHeader.substring(BASIC_PREFIX.length)
            val decodedCredentials = String(Base64.getDecoder().decode(encodedCredentials))
            val parts = decodedCredentials.split(":", limit = 2)
            Pair(parts[0], parts[1])
        } catch (e: Exception) {
            null
        }
    }

    // Validate the credentials against the admin token
    private fun isAdmin(credentials: Pair<String, String>): Boolean {
        return credentials.first == "admin" && credentials.second == applicationProperties.adminToken
    }

    // Validate the credentials against the event password
    private fun handleEventAuthentication(credentials: Pair<String, String>, httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, chain: FilterChain?) {
        val requestURI = httpRequest.requestURI
        val patternResult = EVENT_PATTERN.matchEntire(requestURI)
        patternResult?.groupValues?.get(1)?.toLongOrNull()?.let { eventId ->
            try {
                val event = eventService.getEvent(eventId)
                if (event.password == credentials.second) {
                    continueChain(chain, httpRequest, httpResponse)
                } else {
                    respondWithStatus(httpResponse, FORBIDDEN_MESSAGE, FORBIDDEN_STATUS)
                }
            } catch (e: Exception) {
                respondWithStatus(httpResponse, BAD_REQUEST_MESSAGE, BAD_REQUEST_STATUS)
            }
        } ?: respondWithStatus(httpResponse, UNAUTHORIZED_MESSAGE, UNAUTHORIZED_STATUS)
    }

    // Continue the filter chain if the request is authorized
    private fun continueChain(chain: FilterChain?, request: ServletRequest, response: ServletResponse) {
        chain?.doFilter(request, response)
    }

    // Respond with a status and message if the request is not authorized
    private fun respondWithStatus(response: HttpServletResponse, message: String, status: Int) {
        response.writer?.write(message)
        response.status = status
    }

    // constants
    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BASIC_PREFIX = "Basic "
        private const val UNAUTHORIZED_MESSAGE = "Unauthorized"
        private const val BAD_REQUEST_MESSAGE = "Bad Request"
        private const val FORBIDDEN_MESSAGE = "Forbidden"
        private const val UNAUTHORIZED_STATUS = 401
        private const val BAD_REQUEST_STATUS = 400
        private const val FORBIDDEN_STATUS = 403
        private val EVENT_PATTERN = Regex(".*/rest-api/v1/event/(.+)[/.]*")
    }
}
