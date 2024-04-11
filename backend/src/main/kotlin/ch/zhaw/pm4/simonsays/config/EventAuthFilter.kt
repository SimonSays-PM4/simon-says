package ch.zhaw.pm4.simonsays.config

import ch.zhaw.pm4.simonsays.service.EventService
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.*

class EventAuthFilter(
    private val applicationProperties: ApplicationProperties,
    private val eventService: EventService
) : Filter {

    companion object {
        private val eventPattern = ".*/rest-api/v1/event/(.+)[/.]*".toRegex()
    }

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
            val requestURI = httpRequest.requestURI
            val patternResult = eventPattern.matchEntire(requestURI)
            // Check if the request is for an event
            if (patternResult != null) {
                val eventId = patternResult.groupValues[1]
                try {
                    // extract event id from request and check if event exists
                    val event = eventService.getEvent(eventId.toLong())



                    if (event.password == credentials.second) {
                        chain?.doFilter(request, response)
                        // TODO: Log user access
                        return
                    } else {
                        httpResponse.writer?.write("Forbidden")
                        httpResponse.status = 403
                        return
                    }
                } catch (e: Exception) {
                    httpResponse.writer?.write("Bad Request")
                    httpResponse.status = 400
                    return
                }

            }


            httpResponse.writer?.write("Unauthorized")
            httpResponse.status = 401
            // TODO: return proper error message and log, custom json
        }
    }
}