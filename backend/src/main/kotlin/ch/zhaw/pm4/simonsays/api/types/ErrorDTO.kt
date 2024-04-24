package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema

data class ErrorDTO (
    @field:Schema(description = "Error Code")
    val code: String,
    @field:Schema(description = "Error Message")
    val message: String
)