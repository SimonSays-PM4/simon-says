package ch.zhaw.pm4.simonsays.api.controller.socketio

import jakarta.websocket.server.ServerEndpointConfig
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class WebSocketConfigurator(applicationContext: ApplicationContext?) : ServerEndpointConfig.Configurator() {
    init {
        Companion.applicationContext = applicationContext
    }

    override fun <T> getEndpointInstance(endpointClass: Class<T>): T {
        return applicationContext!!.getBean(endpointClass)
    }

    companion object {
        var applicationContext: ApplicationContext? = null
    }
}