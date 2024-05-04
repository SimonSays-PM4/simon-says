package ch.zhaw.pm4.simonsays.service.printer

import ch.zhaw.pm4.simonsays.api.mapper.printer.PrintQueueJobMapper
import ch.zhaw.pm4.simonsays.api.types.printer.JobStatusDto
import ch.zhaw.pm4.simonsays.api.types.printer.PrintQueueJobDto
import ch.zhaw.pm4.simonsays.entity.printer.JobStatus
import ch.zhaw.pm4.simonsays.repository.printer.PrintQueueJobRepository
import ch.zhaw.pm4.simonsays.repository.printer.PrintQueueRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PrintQueueJobService(
    private val printQueueJobRepository: PrintQueueJobRepository,
    private val printQueueRepository: PrintQueueRepository,
    private val printQueueJobMapper: PrintQueueJobMapper,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun doesPrintQueueJobExist(id: String): Boolean {
        return printQueueJobRepository.existsById(id)
    }

    fun savePrintQueueJob(printerQueueId: String, printQueueJobDto: PrintQueueJobDto): PrintQueueJobDto {
        // Get print queue
        val printQueue = printQueueRepository.findById(printerQueueId)
            .orElseThrow { IllegalArgumentException("Print queue not found") }

        // Update existing print queue job
        val printQueueJob = printQueueJobMapper.mapToPrintQueueJob(printQueueJobDto, printQueue)
        val updatedPrintQueueJob = printQueueJobRepository.saveAndFlush(printQueueJob)
        return printQueueJobMapper.mapToPrintQueueJobDto(updatedPrintQueueJob)
    }

    fun getPrintQueueJobById(jobId: String): PrintQueueJobDto? {
        val printQueueJob = printQueueJobRepository.findById(jobId)
        val printQueueJobDto = printQueueJob.map { printQueueJobMapper.mapToPrintQueueJobDto(it) }
        return printQueueJobDto.orElse(null)
    }

    fun removePrintQueueJob(jobId: String) {
        printQueueJobRepository.deleteById(jobId)
        printQueueJobRepository.flush()
    }

    fun getNextPendingPrintQueueJob(printQueueId: String): PrintQueueJobDto? {
        val printQueue = printQueueRepository.findById(printQueueId)
            .orElseThrow { IllegalArgumentException("Print queue not found") }
        val printQueueJob = printQueueJobRepository.findFirstByPrintQueueAndStatusOrderByCreationDateTimeAsc(
            printQueue, JobStatus.PENDING
        )
        return printQueueJob.map { printQueueJobMapper.mapToPrintQueueJobDto(it) }.orElse(null)
    }

    fun getAllPrintQueueJobsForPrintQueue(printQueueId: String): List<PrintQueueJobDto> {
        val printQueue = printQueueRepository.findById(printQueueId)
            .orElseThrow { IllegalArgumentException("Print queue not found") }
        val printQueueJobs = printQueueJobRepository.findAllByPrintQueue(printQueue)
        return printQueueJobs.map { printQueueJobMapper.mapToPrintQueueJobDto(it) }
    }

    fun getPrintQueueIdForJob(jobId: String): String {
        val printQueueJob = printQueueJobRepository.findById(jobId)
            .orElseThrow { IllegalArgumentException("Print queue job not found") }
        return printQueueJob.printQueue.id
    }
}