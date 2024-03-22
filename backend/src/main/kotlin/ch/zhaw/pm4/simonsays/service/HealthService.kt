package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.HealthDTO


interface HealthService {
    fun showHealth(): HealthDTO

}