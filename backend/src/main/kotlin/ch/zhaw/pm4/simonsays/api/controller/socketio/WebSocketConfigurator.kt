package ch.zhaw.pm4.simonsays.api.controller.socketio

import jakarta.websocket.server.ServerEndpointConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

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