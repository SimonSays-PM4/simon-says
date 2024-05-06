package ch.zhaw.pm4.simonsays.service.printer

import ch.zhaw.pm4.simonsays.repository.printer.PrintQueueRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PrintQueueServiceTest {

    private lateinit var printQueueService: PrintQueueService
    private val printQueueRepository: PrintQueueRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        printQueueService = PrintQueueService(printQueueRepository)
    }

    @Test
    fun `doesPrintQueueExist returns true when print queue exists`() {
        every { printQueueRepository.existsById("existingId") } returns true

        val result = printQueueService.doesPrintQueueExist("existingId")

        assertTrue(result)
    }

    @Test
    fun `doesPrintQueueExist returns false when print queue does not exist`() {
        every { printQueueRepository.existsById("nonExistingId") } returns false

        val result = printQueueService.doesPrintQueueExist("nonExistingId")

        assertFalse(result)
    }
}