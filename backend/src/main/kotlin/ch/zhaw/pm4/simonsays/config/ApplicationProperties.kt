package ch.zhaw.pm4.simonsays.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "application")
class ApplicationProperties {
    var url: String = ""
    var adminToken: String = ""
}