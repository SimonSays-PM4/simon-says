package ch.zhaw.pm4.simonsays.config

import ch.zhaw.pm4.simonsays.api.types.ErrorDTO
import ch.zhaw.pm4.simonsays.config.auth.AuthService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*


@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class AdminEndpoint

@Component
@Order(1)
class AuthFilter(
    val authService: AuthService,
    val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(javaClass)

    // ignore the sonar lint co
    public override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        val method = request.method
        val uri = request.requestURI
        val authorizationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION)
        log.debug("AuthFilter: $method $uri")

        val access = authService.checkRequestAccess(method, uri, authorizationHeaderValue)
        if (access.allowed) {
            filterChain.doFilter(request, response)
        } else {
            respondWithError(request, response, access.httpStatus, access.message)
        }
    }

    /**
     * Respond to the request with an error
     */
    fun respondWithError(
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
}