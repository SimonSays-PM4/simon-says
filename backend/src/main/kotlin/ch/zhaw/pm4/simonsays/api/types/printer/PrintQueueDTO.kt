package ch.zhaw.pm4.simonsays.api.types.printer

import io.swagger.v3.oas.annotations.media.Schema

/*
Terminology:
   ________________              _______________              _________
  |                |  1     n   |               |  n     n   |         |
  | Printer Server |  ------->  | Printer Queue |  ------->  | Printer |
  |________________|            |_______________|            |_________|
                                                  \ 1
                                                   \
                                                    \    n    _______
                                                      ---->  |       |
                                                             |  Job  |
                                                             |_______|

    One printer can in theory be connected to multiple queues, however this is not advices and may
    cause unknown behavior on simultaneous prints.
*/
@Schema(description = "A queue of print jobs for a specific printer.")
data class PrintQueueDTO(
    @Schema(description = "The unique identifier of the print queue.")
    val id: String,

    @Schema(description = "A human readable name mainly used for debugging.")
    val name: String,

    @Schema(description = "The printers that are connected to this queue.")
    val printers: List<PrinterDTO>
)
