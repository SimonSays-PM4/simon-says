package ch.zhaw.pm4.simonsays.service.printer

import ch.zhaw.pm4.simonsays.api.mapper.printer.PrintQueueJobMapper
import ch.zhaw.pm4.simonsays.api.types.printer.PrintQueueJobDTO
import ch.zhaw.pm4.simonsays.entity.printer.JobStatus
import ch.zhaw.pm4.simonsays.entity.printer.PrintQueue
import ch.zhaw.pm4.simonsays.entity.printer.PrintQueueJob
import ch.zhaw.pm4.simonsays.repository.printer.PrintQueueJobRepository
import ch.zhaw.pm4.simonsays.repository.printer.PrintQueueRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

class PrintQueueJobServiceTest {
    private lateinit var service: PrintQueueJobService
    private val printQueueJobRepository: PrintQueueJobRepository = mockk(relaxed = true)
    private val printQueueRepository: PrintQueueRepository = mockk(relaxed = true)
    private val printQueueJobMapper: PrintQueueJobMapper = mockk(relaxed = true)
    private val printQueueJobDto: PrintQueueJobDTO = mockk()
    private val printQueue: PrintQueue = mockk()
    private val printQueueJob: PrintQueueJob = mockk()

    @BeforeEach
    fun setUp() {
        every { printQueueRepository.findById(any()) } returns Optional.of(printQueue)
        every { printQueueJobMapper.mapToPrintQueueJob(any(), printQueue) } returns printQueueJob
        every { printQueueJobRepository.saveAndFlush(printQueueJob) } returns printQueueJob
        every { printQueueJobMapper.mapToPrintQueueJobDto(printQueueJob) } returns printQueueJobDto

        service = PrintQueueJobService(printQueueJobRepository, printQueueRepository, printQueueJobMapper)
    }

    @Test
    fun `test savePrintQueueJob saves and returns job`() {
        val result = service.savePrintQueueJob("queueId", printQueueJobDto)

        assertEquals(printQueueJobDto, result)
        verify { printQueueJobRepository.saveAndFlush(printQueueJob) }
    }

    @Test
    fun `test doesPrintQueueJobExist returns true when job exists`() {
        every { printQueueJobRepository.existsById("jobId") } returns true

        val result = service.doesPrintQueueJobExist("jobId")

        assertTrue(result)
        verify { printQueueJobRepository.existsById("jobId") }
    }

    @Test
    fun `test getPrintQueueJobById returns job when present`() {
        every { printQueueJobRepository.findById("jobId") } returns Optional.of(printQueueJob)
        every { printQueueJobMapper.mapToPrintQueueJobDto(printQueueJob) } returns printQueueJobDto

        val result = service.getPrintQueueJobById("jobId")

        assertEquals(printQueueJobDto, result)
    }

    @Test
    fun `test removePrintQueueJob deletes job`() {
        every { printQueueJobRepository.deleteById("jobId") } answers { Unit }
        every { printQueueJobRepository.flush() } answers { Unit }

        service.removePrintQueueJob("jobId")

        verify { printQueueJobRepository.deleteById("jobId") }
        verify { printQueueJobRepository.flush() }
    }

    @Test
    fun `getNextPendingPrintQueueJob returns job when present`() {
        every { printQueueJobRepository.findFirstByPrintQueueAndStatusOrderByCreationDateTimeAsc(printQueue, JobStatus.PENDING) } returns Optional.of(printQueueJob)
        every { printQueueJobMapper.mapToPrintQueueJobDto(printQueueJob) } returns printQueueJobDto

        val result = service.getNextPendingPrintQueueJob("queueId")

        assertNotNull(result)
        verify { printQueueJobRepository.findFirstByPrintQueueAndStatusOrderByCreationDateTimeAsc(printQueue, JobStatus.PENDING) }
    }

    @Test
    fun `getNextPendingPrintQueueJob returns null when no job is found`() {
        every { printQueueJobRepository.findFirstByPrintQueueAndStatusOrderByCreationDateTimeAsc(printQueue, JobStatus.PENDING) } returns Optional.empty()

        val result = service.getNextPendingPrintQueueJob("queueId")

        assertNull(result)
    }

    @Test
    fun `getAllPrintQueueJobsForPrintQueue returns all jobs`() {
        every { printQueueJobRepository.findAllByPrintQueue(printQueue) } returns listOf(printQueueJob)
        every { printQueueJobMapper.mapToPrintQueueJobDto(printQueueJob) } returns printQueueJobDto

        val result = service.getAllPrintQueueJobsForPrintQueue("queueId")

        assertEquals(listOf(printQueueJobDto), result)
        verify { printQueueJobRepository.findAllByPrintQueue(printQueue) }
    }

    @Test
    fun `getPrintQueueIdForJob returns the correct queue ID`() {
        every { printQueueJobRepository.findById("jobId") } returns Optional.of(printQueueJob)
        every { printQueueJob.printQueue } returns printQueue
        every { printQueue.id } returns "queueId"

        val result = service.getPrintQueueIdForJob("jobId")

        assertEquals("queueId", result)
        verify { printQueueJobRepository.findById("jobId") }
    }

    @Test
    fun `getPrintQueueIdForJob throws exception when job is not found`() {
        every { printQueueJobRepository.findById("jobId") } returns Optional.empty()

        var exception: IllegalArgumentException? = null
        try {
            service.getPrintQueueIdForJob("jobId")
        } catch (e: IllegalArgumentException) {
            exception = e
        }

        assertNotNull(exception)
        assertEquals("Print queue job not found", exception?.message)
    }
}
