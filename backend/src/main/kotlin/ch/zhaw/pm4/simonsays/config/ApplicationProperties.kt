package ch.zhaw.pm4.simonsays.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application")
class ApplicationProperties (
    val url: String
)