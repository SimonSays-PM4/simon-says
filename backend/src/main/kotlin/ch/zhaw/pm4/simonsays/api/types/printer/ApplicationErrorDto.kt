package ch.zhaw.pm4.simonsays.api.types.printer

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "An error response.")
data class ApplicationErrorDto(
    @Schema(description = "The error code.")
    val code: String,

    @Schema(description = "The error message.")
    val message: String,
)