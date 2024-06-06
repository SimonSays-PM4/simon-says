package ch.zhaw.pm4.simonsays.api.controller.socketio

import jakarta.websocket.server.ServerEndpointConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

/**
 * This class is required to inject Spring beans into WebSocket endpoints.
 *
 * To use it in a WebSocket endpoint, add the following annotation to the endpoint class:
 *
 * "@ServerEndpoint("/socket.io/", **configurator = WebSocketConfigurator::class**)"
 *
 */
@Component
class WebSocketConfigurator : ServerEndpointConfig.Configurator() {
    @Autowired
    fun setApplicationContext(applicationContext: ApplicationContext?) {
        Companion.applicationContext = applicationContext
    }

    override fun <T> getEndpointInstance(endpointClass: Class<T>): T {
        return applicationContext!!.getBean(endpointClass)
    }

    companion object {
        var applicationContext: ApplicationContext? = null
    }
}