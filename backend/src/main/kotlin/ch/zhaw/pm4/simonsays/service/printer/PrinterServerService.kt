package ch.zhaw.pm4.simonsays.service.printer

import ch.zhaw.pm4.simonsays.api.mapper.printer.PrinterServerMapper
import ch.zhaw.pm4.simonsays.api.types.printer.PrintQueueDto
import ch.zhaw.pm4.simonsays.api.types.printer.PrinterDto
import ch.zhaw.pm4.simonsays.api.types.printer.PrinterServerDto
import ch.zhaw.pm4.simonsays.repository.printer.PrintQueueRepository
import ch.zhaw.pm4.simonsays.repository.printer.PrinterServerRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull


@Service
class PrinterServerService(
    private val printerServerRepository: PrinterServerRepository,
    private val printQueueRepository: PrintQueueRepository,
    private val printerServerMapper: PrinterServerMapper,
) {
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
        val updatedPrinterServer = printerServerRepository.saveAndFlush(printerServer)
        return printerServerMapper.mapToPrinterServerDto(updatedPrinterServer)
    }

    fun removePrinterServer(id: String) {
        printerServerRepository.deleteById(id)
        printerServerRepository.flush()
    }

    fun createSampleData() {
        val printer1 = PrinterDto(
            mac = "00:00:00:00:00:01",
            name = "Sample Printer 1",
        )
        val printer2 = PrinterDto(
            mac = "00:00:00:00:00:02",
            name = "Sample Printer 2",
        )
        val samplePrintQueue1 = PrintQueueDto(
            id = "eb589790-b209-4e53-adc4-34da3ce89f01",
            name = "sample-print-queue-1",
            printers = listOf(
                printer1, printer2
            ),
        )
        val samplePrintQueue2 = PrintQueueDto(
            id = "98de5781-0ef8-4278-95de-cb3e2e513e64",
            name = "sample-print-queue-2",
            printers = emptyList(),
        )
        val samplePrinterServer1 = PrinterServerDto(
            id = "eb6c2108-82c2-4260-93b7-6a0937cf73ef",
            name = "Sample Printer Server",
            queues = listOf(samplePrintQueue1, samplePrintQueue2),
        )
        savePrinterServer(samplePrinterServer1)

        val samplePrinterServer2 = PrinterServerDto(
            id = "3ef0f613-13fb-4eb9-a9be-df86db516615",
            name = "Sample Printer Server 2",
            queues = listOf(),
        )
        savePrinterServer(samplePrinterServer2)

        val samplePrintQueue3 = PrintQueueDto(
            id = "6218afd5-0fac-4826-afe3-115c103daf67",
            name = "sample-print-queue-3",
            printers = listOf(
                printer1
            ),
        )
        val samplePrinterServer3 = PrinterServerDto(
            id = "73e0c3dd-c188-4e4b-8e50-752ef9712604",
            name = "Sample Printer Server 3",
            queues = listOf(samplePrintQueue3),
        )
        savePrinterServer(samplePrinterServer3)
    }

    init {
        // TODO(Lukas) REMOVE
        println("Creating sample data")
        createSampleData()
    }
}