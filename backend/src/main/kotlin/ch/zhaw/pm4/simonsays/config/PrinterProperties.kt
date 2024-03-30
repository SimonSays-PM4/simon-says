package ch.zhaw.pm4.simonsays.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "printer-queue-server")
class PrinterProperties {
    lateinit var frontendOrigins: Array<String>
}