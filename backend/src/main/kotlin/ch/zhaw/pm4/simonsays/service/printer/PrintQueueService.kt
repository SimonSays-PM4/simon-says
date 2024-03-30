package ch.zhaw.pm4.simonsays.service.printer

import ch.zhaw.pm4.simonsays.api.mapper.printer.PrintQueueMapper
import ch.zhaw.pm4.simonsays.api.types.printer.PrintQueueDto
import ch.zhaw.pm4.simonsays.repository.printer.PrintQueueRepository
import org.springframework.stereotype.Service

@Service
class PrintQueueService(
    private val printQueueRepository: PrintQueueRepository,
    private val printQueueMapper: PrintQueueMapper,
) {
    fun getAllPrintQueuesForPrintServer(printServerId: String): List<PrintQueueDto> {
        val queues = printQueueRepository.findAllByPrinterServerId(printServerId)
        return queues.map { printQueueMapper.mapToPrintQueueDto(it) }
    }

    fun getPrintQueueById(printServerId: String, queueId: String): PrintQueueDto? {
        val queue = printQueueRepository.getByPrinterServerIdAndId(printServerId, queueId)
        return queue?.let { printQueueMapper.mapToPrintQueueDto(it) }
    }
}