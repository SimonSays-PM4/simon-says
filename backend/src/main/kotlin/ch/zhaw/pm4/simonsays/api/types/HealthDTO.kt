package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema

data class HealthDTO (
    @field:Schema(description = "Shows the state of the application.")
    val state: String
)