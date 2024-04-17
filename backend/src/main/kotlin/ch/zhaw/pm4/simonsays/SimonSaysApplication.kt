package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.config.ApplicationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan

@SpringBootApplication
@ServletComponentScan
@EnableConfigurationProperties(ApplicationProperties::class)
class SimonSaysApplication

fun main(args: Array<String>) {
    runApplication<SimonSaysApplication>(*args)
}
