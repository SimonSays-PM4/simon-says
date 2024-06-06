package ch.zhaw.pm4.simonsays.api.mapper.printer

import ch.zhaw.pm4.simonsays.api.types.printer.PrintQueueJobDTO
import ch.zhaw.pm4.simonsays.entity.printer.PrintQueue
import ch.zhaw.pm4.simonsays.entity.printer.PrintQueueJob
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface PrintQueueJobMapper {
    fun mapToPrintQueueJobDto(printQueueJob: PrintQueueJob): PrintQueueJobDTO

    @Mapping(target = "printQueue", source = "printerQueue")
    @Mapping(target = "id", source = "printQueueJobDto.id")
    fun mapToPrintQueueJob(printQueueJobDto: PrintQueueJobDTO, printerQueue: PrintQueue): PrintQueueJob
}