package ch.zhaw.pm4.simonsays.api.types.printer

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "An update for a print queue job's status.")
data class PrintQueueJobUpdateDto(
    @Schema(description = "The unique identifier of the print job to update.")
    val id: String,

    @Schema(description = "The new status for the print job.", enumAsRef = true)
    val status: JobStatusDto,

    @Schema(description = "An optional message describing the new status of the print job.", required = false)
    val statusMessage: String? = null
)