package ch.zhaw.pm4.simonsays.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "application")
class ApplicationProperties {
    lateinit var frontendOrigins: Array<String>
    var url: String = ""
    var adminToken: String = ""
}