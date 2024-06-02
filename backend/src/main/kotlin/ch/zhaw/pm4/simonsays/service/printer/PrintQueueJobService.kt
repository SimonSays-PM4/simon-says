package ch.zhaw.pm4.simonsays.service.printer

import ch.zhaw.pm4.simonsays.api.mapper.printer.PrintQueueJobMapper
import ch.zhaw.pm4.simonsays.api.types.printer.PrintQueueJobDTO
import ch.zhaw.pm4.simonsays.entity.printer.JobStatus
import ch.zhaw.pm4.simonsays.repository.printer.PrintQueueJobRepository
import ch.zhaw.pm4.simonsays.repository.printer.PrintQueueRepository
import org.springframework.stereotype.Service

private const val printQueueNotFoundError = "Print queue not found"
private const val printQueueJobNotFoundError = "Print queue job not found"

@Service
class PrintQueueJobService(
    private val printQueueJobRepository: PrintQueueJobRepository,
    private val printQueueRepository: PrintQueueRepository,
    private val printQueueJobMapper: PrintQueueJobMapper,
) {
    fun doesPrintQueueJobExist(id: String): Boolean {
        return printQueueJobRepository.existsById(id)
    }

    fun savePrintQueueJob(printerQueueId: String, printQueueJobDto: PrintQueueJobDTO): PrintQueueJobDTO {
        // Get print queue
        val printQueue = printQueueRepository.findById(printerQueueId)
            .orElseThrow { IllegalArgumentException(printQueueNotFoundError) }

        // Update existing print queue job
        val printQueueJob = printQueueJobMapper.mapToPrintQueueJob(printQueueJobDto, printQueue)
        val updatedPrintQueueJob = printQueueJobRepository.saveAndFlush(printQueueJob)
        return printQueueJobMapper.mapToPrintQueueJobDto(updatedPrintQueueJob)
    }

    fun getPrintQueueJobById(jobId: String): PrintQueueJobDTO? {
        val printQueueJob = printQueueJobRepository.findById(jobId)
        val printQueueJobDto = printQueueJob.map { printQueueJobMapper.mapToPrintQueueJobDto(it) }
        return printQueueJobDto.orElse(null)
    }

    fun removePrintQueueJob(jobId: String) {
        printQueueJobRepository.deleteById(jobId)
        printQueueJobRepository.flush()
    }

    fun getNextPendingPrintQueueJob(printQueueId: String): PrintQueueJobDTO? {
        val printQueue = printQueueRepository.findById(printQueueId)
            .orElseThrow { IllegalArgumentException(printQueueNotFoundError) }
        val printQueueJob = printQueueJobRepository.findFirstByPrintQueueAndStatusOrderByCreationDateTimeAsc(
            printQueue, JobStatus.PENDING
        )
        return printQueueJob.map { printQueueJobMapper.mapToPrintQueueJobDto(it) }.orElse(null)
    }

    fun getAllPrintQueueJobsForPrintQueue(printQueueId: String): List<PrintQueueJobDTO> {
        val printQueue = printQueueRepository.findById(printQueueId)
            .orElseThrow { IllegalArgumentException(printQueueNotFoundError) }
        val printQueueJobs = printQueueJobRepository.findAllByPrintQueue(printQueue)
        return printQueueJobs.map { printQueueJobMapper.mapToPrintQueueJobDto(it) }
    }

    fun getPrintQueueIdForJob(jobId: String): String {
        val printQueueJob = printQueueJobRepository.findById(jobId)
            .orElseThrow { IllegalArgumentException(printQueueJobNotFoundError) }
        return printQueueJob.printQueue.id
    }
}