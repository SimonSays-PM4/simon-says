package ch.zhaw.pm4.simonsays.api.mapper.printer

import ch.zhaw.pm4.simonsays.api.types.printer.PrintQueueDto
import ch.zhaw.pm4.simonsays.entity.printer.PrintQueue
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface PrintQueueMapper {
    fun mapToPrintQueueDto(printQueue: PrintQueue): PrintQueueDto
    fun mapToPrintQueue(printQueueDto: PrintQueueDto): PrintQueue
}