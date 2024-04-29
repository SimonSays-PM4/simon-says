package ch.zhaw.pm4.simonsays.service.printer

import ch.zhaw.pm4.simonsays.api.mapper.printer.PrinterServerMapper
import ch.zhaw.pm4.simonsays.api.types.printer.PrintQueueDto
import ch.zhaw.pm4.simonsays.api.types.printer.PrinterDto
import ch.zhaw.pm4.simonsays.api.types.printer.PrinterServerDto
import ch.zhaw.pm4.simonsays.config.PrinterProperties
import ch.zhaw.pm4.simonsays.repository.printer.PrintQueueRepository
import ch.zhaw.pm4.simonsays.repository.printer.PrinterServerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull


@Service
class PrinterServerService(
    private val printerServerRepository: PrinterServerRepository,
    private val printerServerMapper: PrinterServerMapper,
    private val printerProperties: PrinterProperties,
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

    private final fun createInitialData() {
        val takeawayPrinter = PrinterDto(
            mac = printerProperties.takeawayPrinterMac,
            name = "Takeaway Printer",

        )
        val takeawayPrintQueue = PrintQueueDto(
            id = printerProperties.takeawayPrinterQueueId,
            name = "Takeaway Print Queue",
            printers = listOf(
                takeawayPrinter,
            ),
        )
        val receiptPrinter = PrinterDto(
            mac = printerProperties.receiptPrinterMac,
            name = "Receipt Printer",
        )
        val receiptPrintQueue = PrintQueueDto(
            id = printerProperties.receiptPrinterQueueId,
            name = "Receipt Print Queue",
            printers = listOf(
                receiptPrinter,
            ),
        )
        val printerServer = PrinterServerDto(
            id = printerProperties.printerServerId,
            name = "Printer Server",
            queues = listOf(
                takeawayPrintQueue,
                receiptPrintQueue,
            ),
        )
        savePrinterServer(printerServer)
        log.debug("Created initial printer data")
    }

    init {
        if (printerProperties.createInitialData) {
            log.debug("Creating initial printer data")
            createInitialData()
        } else {
            log.debug("Not creating initial printer data")
        }
    }
}