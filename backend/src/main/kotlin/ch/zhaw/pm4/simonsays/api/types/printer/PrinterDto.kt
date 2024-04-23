package ch.zhaw.pm4.simonsays.api.types.printer

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "A printer connected to a print queue.")
data class PrinterDto(
    @Schema(description = "A human readable name mainly used for debugging.")
    val name: String,

    @Schema(description = "The MAC address of the printer, serving as a unique identifier.")
    val mac: String
)