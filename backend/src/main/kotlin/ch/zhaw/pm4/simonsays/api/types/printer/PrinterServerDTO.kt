package ch.zhaw.pm4.simonsays.api.types.printer

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "A Printer server with the respective print queues.")
data class PrinterServerDTO(
    @Schema(description = "The unique identifier of the printer server.")
    val id: String,

    @Schema(description = "A human readable name mainly used for debugging.")
    val name: String,

    @Schema(description = "The queues that are connected to this printer server.")
    val queues: List<PrintQueueDTO>
)