package ch.zhaw.pm4.simonsays.service.printer

import ch.zhaw.pm4.simonsays.api.mapper.printer.PrinterServerMapper
import ch.zhaw.pm4.simonsays.api.types.printer.PrintQueueDto
import ch.zhaw.pm4.simonsays.api.types.printer.PrinterDto
import ch.zhaw.pm4.simonsays.api.types.printer.PrinterServerDto
import ch.zhaw.pm4.simonsays.repository.printer.PrinterServerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull


@Service
class PrinterServerService(
    private val printerServerRepository: PrinterServerRepository,
    private val printerServerMapper: PrinterServerMapper,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun getAllPrinterServers(): List<PrinterServerDto> {
        val printerServers = printerServerRepository.findAll()
        return printerServers.map { printerServerMapper.mapToPrinterServerDto(it) }
    }

    fun getPrinterServerById(id: String): PrinterServerDto? {
        val printerServer = printerServerRepository.findById(id)
        val printerServerDto = printerServer.map { printerServerMapper.mapToPrinterServerDto(it) }
        return printerServerDto.getOrNull()
    }

    fun doesPrinterServerExist(id: String): Boolean {
        return printerServerRepository.existsById(id)
    }

    fun savePrinterServer(printerServerDto: PrinterServerDto): PrinterServerDto {
        // Update existing printer server
        val printerServer = printerServerMapper.mapToPrinterServer(printerServerDto)
        val updatedPrinterServer = printerServerRepository.save(printerServer)
        return printerServerMapper.mapToPrinterServerDto(updatedPrinterServer)
    }

    fun removePrinterServer(id: String) {
        printerServerRepository.deleteById(id)
        printerServerRepository.flush()
    }

    private final fun createSampleData() {
        val printer1 = PrinterDto(
            mac = "00:00:00:00:00:01",
            name = "Sample Printer",
        )
        val samplePrintQueue = PrintQueueDto(
            id = "eb589790-b209-4e53-adc4-34da3ce89f01",
            name = "Sample Print Queue",
            printers = listOf(
                printer1
            ),
        )
        val samplePrinterServer1 = PrinterServerDto(
            id = "eb6c2108-82c2-4260-93b7-6a0937cf73ef",
            name = "Sample Printer Server",
            queues = listOf(samplePrintQueue),
        )
        savePrinterServer(samplePrinterServer1)
    }

    init {
        log.debug("Creating sample data")
        createSampleData()
    }

}