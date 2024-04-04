package ch.zhaw.pm4.simonsays.api.mapper.printer

import ch.zhaw.pm4.simonsays.api.types.printer.PrintQueueJobDto
import ch.zhaw.pm4.simonsays.entity.printer.PrintQueueJob
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface PrintQueueJobMapper {
    fun mapToPrintQueueJobDto(printQueueJob: PrintQueueJob): PrintQueueJobDto
    // TODO(LUKAS) Doesn't work, not sure how we can fix this
    fun mapToPrintQueueJob(printQueueJobDto: PrintQueueJobDto): PrintQueueJob
}