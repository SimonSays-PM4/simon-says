package ch.zhaw.pm4.simonsays.api.controller.socketio

import io.socket.engineio.server.EngineIoServer
import io.socket.engineio.server.EngineIoServerOptions
import io.socket.socketio.server.SocketIoServer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.server.standard.ServerEndpointExporter



@Configuration
@EnableWebSocket
class SocketIoConfig {
    @Bean
    fun engineIoServer(): EngineIoServer {
        return EngineIoServer(EngineIoServerOptions.newFromDefault().apply {
            isCorsHandlingDisabled = true
        })
    }

    @Bean
    fun socketIoServer(engineIoServer: EngineIoServer): SocketIoServer {
        return SocketIoServer(engineIoServer)
    }

    @Bean
    fun serverEndpointExporter(): ServerEndpointExporter {
        return ServerEndpointExporter()
    }
}