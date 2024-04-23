package ch.zhaw.pm4.simonsays.service.printer

import ch.zhaw.pm4.simonsays.repository.printer.PrintQueueRepository
import org.springframework.stereotype.Service

@Service
class PrintQueueService(
    private val printQueueRepository: PrintQueueRepository,
) {

    fun doesPrintQueueExist(id: String): Boolean {
        return printQueueRepository.existsById(id)
    }
}