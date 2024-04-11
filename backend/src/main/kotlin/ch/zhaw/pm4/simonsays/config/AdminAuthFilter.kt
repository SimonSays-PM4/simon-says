package ch.zhaw.pm4.simonsays.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.*

class AdminAuthFilter(
    private val applicationProperties: ApplicationProperties
) : Filter {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        var basicAuthToken = httpRequest.getHeader("Authorization")
        if (basicAuthToken == null || !basicAuthToken.startsWith("Basic ")) {
            httpResponse.writer?.write("Unauthorized")
            httpResponse.status = 401
            return
        }

        val credentials = try {
            basicAuthToken = basicAuthToken.substring(6)
            val decodedAuthToken = Base64.getDecoder().decode(basicAuthToken)
            val decodedCredentials = String(decodedAuthToken)
            val parts = decodedCredentials.split(":", limit = 2)
            val username = parts[0]
            val password = parts[1]
            Pair(username, password)
        } catch (e: Exception) {
            httpResponse.writer?.write("Bad request")
            httpResponse.status = 400
            return
        }

        if (credentials.first == "admin" && credentials.second == applicationProperties.adminToken) {
            chain?.doFilter(request, response)
        } else {
            httpResponse.writer?.write("Unauthorized")
            httpResponse.status = 401
            // TODO: return proper error message and log, custom json
        }
    }
}