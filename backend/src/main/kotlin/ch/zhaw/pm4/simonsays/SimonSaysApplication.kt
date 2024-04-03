package ch.zhaw.pm4.simonsays

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan

@SpringBootApplication
@ServletComponentScan
class SimonSaysApplication

fun main(args: Array<String>) {
    runApplication<SimonSaysApplication>(*args)
}
