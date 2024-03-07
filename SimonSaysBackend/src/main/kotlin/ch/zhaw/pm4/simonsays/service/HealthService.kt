package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.Health


interface HealthService {
    fun showHealth(): Health

}