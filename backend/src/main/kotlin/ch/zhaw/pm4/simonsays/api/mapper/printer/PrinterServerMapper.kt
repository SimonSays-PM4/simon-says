package ch.zhaw.pm4.simonsays.api.mapper.printer

import ch.zhaw.pm4.simonsays.api.types.printer.PrinterServerDto
import ch.zhaw.pm4.simonsays.entity.printer.PrinterServer
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface PrinterServerMapper {

    fun mapToPrinterServerDto(printerServer: PrinterServer): PrinterServerDto

    fun mapToPrinterServer(printerServerDto: PrinterServerDto): PrinterServer
}