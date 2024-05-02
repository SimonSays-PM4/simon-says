package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.HealthDTO
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.util.*

@Service
class HealthService {
    private val resource = ClassPathResource("version.properties")
    private final val properties = Properties().apply {
        load(resource.inputStream)
    }

    private val version: String = properties.getProperty("version")

    fun showHealth() = HealthDTO("up", version)
}