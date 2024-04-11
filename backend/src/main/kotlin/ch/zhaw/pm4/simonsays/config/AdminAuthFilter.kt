package ch.zhaw.pm4.simonsays.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.*

class AdminAuthFilter(private val applicationProperties: ApplicationProperties) : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        // Check if the request is authorized
        val credentials = extractCredentials(httpRequest.getHeader(AUTHORIZATION_HEADER))
        if (!credentials.isPresent) {
            respondWithUnauthorized(httpResponse)
            return
        }

        // Check if the credentials are valid
        if (validateCredentials(credentials.get())) {
            continueChain(chain, request, response)
        } else {
            respondWithUnauthorized(httpResponse)
        }
    }

    // Extract the credentials from the Authorization header
    private fun extractCredentials(authHeader: String?): Optional<String> {
        if (authHeader == null || !authHeader.startsWith(BASIC_PREFIX)) {
            return Optional.empty()
        }
        try {
            val encodedCredentials = authHeader.substring(BASIC_PREFIX.length)
            val decodedCredentials = String(Base64.getDecoder().decode(encodedCredentials))
            return Optional.of(decodedCredentials)
        } catch (e: IllegalArgumentException) {
            return Optional.empty()
        }
    }

    // Validate the credentials against the admin token
    private fun validateCredentials(credentials: String): Boolean {
        val parts = credentials.split(":".toRegex(), limit = 2).toTypedArray()
        if (parts.size < 2) return false
        val username = parts[0]
        val password = parts[1]
        return "admin" == username && applicationProperties.adminToken == password
    }

    // Continue the filter chain if the request is authorized
    private fun continueChain(chain: FilterChain, request: ServletRequest, response: ServletResponse) {
        try {
            chain.doFilter(request, response)
        } catch (e: Exception) {
            throw RuntimeException("Filter Chain Exception", e)
        }
    }

    // Respond with an unauthorized status if the request is not authorized
    private fun respondWithUnauthorized(response: HttpServletResponse) {
        try {
            response.status = UNAUTHORIZED_STATUS
            response.writer.write(UNAUTHORIZED_MESSAGE)
        } catch (e: Exception) {
            throw RuntimeException("Response Writing Exception", e)
        }
    }

    // constants
    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BASIC_PREFIX = "Basic "
        private const val UNAUTHORIZED_MESSAGE = "Unauthorized"
        private const val UNAUTHORIZED_STATUS = 401
        private const val BAD_REQUEST_STATUS = 400
    }
}