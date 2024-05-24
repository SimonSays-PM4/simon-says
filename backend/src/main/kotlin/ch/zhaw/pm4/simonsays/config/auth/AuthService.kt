package ch.zhaw.pm4.simonsays.config.auth

import ch.zhaw.pm4.simonsays.config.AdminEndpoint
import ch.zhaw.pm4.simonsays.config.ApplicationProperties
import ch.zhaw.pm4.simonsays.config.PrinterProperties
import ch.zhaw.pm4.simonsays.service.EventService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.AntPathMatcher
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.*

@Service
class AuthService(
    val applicationProperties: ApplicationProperties,
    val printerProperties: PrinterProperties,
    val eventService: EventService,
    requestMappingHandlerMapping: RequestMappingHandlerMapping
) {
    private val matcher = AntPathMatcher()
    private val log = LoggerFactory.getLogger(javaClass)

    final val adminEndpoints: Map<RequestMappingInfo, HandlerMethod> =
        requestMappingHandlerMapping.handlerMethods // Get all handler methods
            .filter { it.value.method.isAnnotationPresent(AdminEndpoint::class.java) } // Filter for methods annotated with @AdminEndpoint

    /**
     * Check if the given request is allowed to access the endpoint.
     *
     * @param method The HTTP method of the request. May be null if the method is not present.
     */
    fun checkRequestAccess(method: String? = null, uri: String, authorizationHeaderValue: String?): Access {/*
        * Developers note: This method may seem a bit complex and long at first glance. However, it is structured with
        * the "trap doors" pattern in mind and allows us to have all high level access checks within one method.
        * This makes it easier to understand and maintain the access control logic within the application because we
        * have one entry point for all access control checks.
        */

        // We allow OPTIONS requests without authentication to allow CORS preflight requests
        if (method == HttpMethod.OPTIONS.name()) {
            return Access.ALLOWED_OPTIONS
        }

        val isAdminEndpoint = isAdminEndpoint(method, uri)
        val isEventRelatedEndpoint = isEventRelatedEndpoint(uri)
        val isPrinterRelatedEndpoint = isPrinterRelatedEndpoint(uri)

        // In case the request is not targeting any event, admin or printer related endpoint, we allow it
        if (!isAdminEndpoint && !isEventRelatedEndpoint && !isPrinterRelatedEndpoint) {
            return Access.ALLOWED_UNRELATED_REQUEST
        }

        if (authorizationHeaderValue.isNullOrBlank()) {
            return Access.UNAUTHORIZED
        }

        // Check for printer authorization
        if (isPrinterRelatedEndpoint) {
            if (isPrinterTokenValid(authorizationHeaderValue)) {
                return Access.ALLOWED
            }
            return Access.FORBIDDEN_PRINTER
        }

        if (!authorizationHeaderValue.startsWith(BASIC_AUTH_PREFIX)) {
            return Access.BAD_REQUEST
        }

        val (username, password) = try {
            extractCredentials(authorizationHeaderValue)
        } catch (exception: Exception) {
            return Access.BAD_REQUEST
        }

        // Check for admin authorization
        val isAdmin = isAdmin(username, password)
        if (isAdminEndpoint) {
            if (isAdmin) {
                return Access.ALLOWED
            }
            return Access.FORBIDDEN_ADMIN_ENDPOINT
        }

        // If the request is neither a printer nor admin endpoint it has to be an event endpoint.

        // In case the event id is not present in the uri, we return forbidden
        val eventId = getEventIdFromUri(uri) ?: return Access.FORBIDDEN_EVENT

        // Admin is allowed to access all events.
        if (isAdmin) {
            return Access.ALLOWED
        }

        // Check if the username is allowed
        if (!isUsernameAllowed(username)) {
            return Access.FORBIDDEN_ILLEGAL_USERNAME
        }

        // Check if the event credentials are valid
        if (!areEventCredentialsValid(eventId, password)) {
            return Access.FORBIDDEN_EVENT
        }

        return Access.ALLOWED
    }


    /**
     * Check if the given request is targeting an admin endpoint
     */
    private fun isAdminEndpoint(method: String?, uri: String): Boolean {
        // If the method is not present, we return false
        if (method.isNullOrBlank()) {
            return false
        }
        for (entry in adminEndpoints) {
            val requestMappingInfo = entry.key
            // Check if the request method matches
            if (requestMappingInfo.methodsCondition.methods.none { it.name == method }) {
                continue
            }

            // Check if the request uri matches
            for (pattern in requestMappingInfo.patternValues) {
                if (matcher.match(pattern, uri)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Check if the given request uri is an event related endpoint
     */
    private fun isEventRelatedEndpoint(uri: String): Boolean {
        return matcher.match(EVENT_ANT_PATTERN, uri)
    }

    /**
     * Check if the given request uri is a printer related endpoint
     */
    private fun isPrinterRelatedEndpoint(uri: String): Boolean {
        return matcher.match(PRINTER_ANT_PATTERN, uri)
    }

    /**
     * Extracts the credentials from the Authorization header
     */
    private fun extractCredentials(authorizationHeaderValue: String): Pair<String, String> {
        val encodedCredentials = authorizationHeaderValue.substring(BASIC_AUTH_PREFIX.length)
        val decodedCredentials = String(Base64.getDecoder().decode(encodedCredentials))
        val parts = decodedCredentials.split(":", limit = 2)
        return Pair(parts[0], parts[1])
    }

    /**
     * Check if the given credentials are admin
     */
    private fun isAdmin(username: String, token: String): Boolean {
        return username == ADMIN_USERNAME && token == applicationProperties.adminToken
    }

    /**
     * Check if the given printer token is valid
     */
    private fun isPrinterTokenValid(token: String?): Boolean {
        return token == printerProperties.printerAccessTokenA || token == printerProperties.printerAccessTokenB
    }

    /**
     * Get the event id from an event related endpoint
     */
    private fun getEventIdFromUri(requestURI: String): Long? {
        val match = matcher.extractUriTemplateVariables(EVENT_ANT_PATTERN, requestURI)
        return match["eventId"]?.toLongOrNull()
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
}

private const val BASIC_AUTH_PREFIX = "Basic "
private const val ADMIN_USERNAME = "admin"
private const val EVENT_ANT_PATTERN = "/*-api/v1/event/{eventId}/**"
private const val PRINTER_ANT_PATTERN = "/socket-api/v1/printer-servers/**"

enum class Access(val allowed: Boolean, val httpStatus: HttpStatus, val message: String) {
    ALLOWED(true, HttpStatus.OK, "Access allowed."), //
    ALLOWED_OPTIONS(
        true, HttpStatus.OK, "Access allowed for OPTIONS request."
    ),
    ALLOWED_UNRELATED_REQUEST(
        true, HttpStatus.OK, "Granted access because the request is not related to an event or admin endpoint."
    ),
    UNAUTHORIZED(false, HttpStatus.UNAUTHORIZED, "No Authorization was provided."), //
    BAD_REQUEST(
        false,
        HttpStatus.BAD_REQUEST,
        "Invalid Authorization format. Make sure to start the token with '$BASIC_AUTH_PREFIX' and is of type basic auth."
    ),
    FORBIDDEN_ADMIN_ENDPOINT(
        false, HttpStatus.FORBIDDEN, "Forbidden access to admin endpoint."
    ),
    FORBIDDEN_PRINTER(
        false, HttpStatus.FORBIDDEN, "Forbidden access to printer related endpoint."
    ),
    FORBIDDEN_EVENT(
        false, HttpStatus.FORBIDDEN, "Forbidden access to event or event does not exist"
    ),
    FORBIDDEN_ILLEGAL_USERNAME(
        false, HttpStatus.FORBIDDEN, "Username is not allowed (cannot be admin or empty)"
    ),
    FORBIDDEN_UNMATCHED_REQUEST(false, HttpStatus.FORBIDDEN, "Unable to match request to any endpoint.")
}

