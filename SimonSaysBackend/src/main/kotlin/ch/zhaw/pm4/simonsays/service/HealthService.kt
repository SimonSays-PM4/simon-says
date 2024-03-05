package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.Health
import org.springframework.stereotype.Service

@Service
class HealthService {

    fun showHealth() = Health("up")
}