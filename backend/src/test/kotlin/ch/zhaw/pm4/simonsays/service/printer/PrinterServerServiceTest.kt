package ch.zhaw.pm4.simonsays.service.printer

import ch.zhaw.pm4.simonsays.api.mapper.printer.PrinterServerMapper
import ch.zhaw.pm4.simonsays.api.types.printer.PrinterServerDto
import ch.zhaw.pm4.simonsays.config.PrinterProperties
import ch.zhaw.pm4.simonsays.entity.printer.PrinterServer
import ch.zhaw.pm4.simonsays.repository.printer.PrintQueueRepository
import ch.zhaw.pm4.simonsays.repository.printer.PrinterServerRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import java.util.Optional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PrinterServerServiceTest {

    private lateinit var service: PrinterServerService
    private val printerServerRepository: PrinterServerRepository = mockk(relaxed = true)
    private val printerServerMapper: PrinterServerMapper = mockk(relaxed = true)
    private val printerProperties: PrinterProperties = mockk(relaxed = true)

    private val printerServer: PrinterServer = mockk(relaxed = true)
    private val printerServerDto: PrinterServerDto = mockk(relaxed = true)
    private val printQueueRepository: PrintQueueRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        service = PrinterServerService(printerServerRepository, printQueueRepository, printerServerMapper, printerProperties)
    }

    @Test
    fun `getAllPrinterServers returns all printer servers`() {
        every { printerServerRepository.findAll() } returns listOf(printerServer)
        every { printerServerMapper.mapToPrinterServerDto(printerServer) } returns printerServerDto

        val result = service.getAllPrinterServers()

        assertNotNull(result)
        assertEquals(listOf(printerServerDto), result)
        verify { printerServerRepository.findAll() }
    }

    @Test
    fun `getPrinterServerById returns a printer server when present`() {
        every { printerServerRepository.findById("id") } returns Optional.of(printerServer)
        every { printerServerMapper.mapToPrinterServerDto(printerServer) } returns printerServerDto

        val result = service.getPrinterServerById("id")

        assertNotNull(result)
        assertEquals(printerServerDto, result)
        verify { printerServerRepository.findById("id") }
    }

    @Test
    fun `getPrinterServerById returns null when printer server is not found`() {
        every { printerServerRepository.findById("id") } returns Optional.empty()

        val result = service.getPrinterServerById("id")

        assertNull(result)
    }

    @Test
    fun `doesPrinterServerExist returns true when printer server exists`() {
        every { printerServerRepository.existsById("id") } returns true

        val result = service.doesPrinterServerExist("id")

        assertEquals(true, result)
        verify { printerServerRepository.existsById("id") }
    }

    @Test
    fun `doesPrinterServerExist returns false when printer server does not exist`() {
        every { printerServerRepository.existsById("id") } returns false

        val result = service.doesPrinterServerExist("id")

        assertFalse(result)
    }

    @Test
    fun `savePrinterServer saves and returns printer server`() {
        every { printerServerMapper.mapToPrinterServer(printerServerDto) } returns printerServer
        every { printerServerRepository.save(printerServer) } returns printerServer
        every { printerServerMapper.mapToPrinterServerDto(printerServer) } returns printerServerDto

        val result = service.savePrinterServer(printerServerDto)

        assertEquals(printerServerDto, result)
        verify { printerServerRepository.save(printerServer) }

    }

    @Test
    fun `removePrinterServer deletes a printer server`() {
        every { printerServerRepository.deleteById("id") } answers { Unit }
        every { printerServerRepository.flush() } answers { Unit }

        service.removePrinterServer("id")

        verify { printerServerRepository.deleteById("id") }
        verify { printerServerRepository.flush() }
    }
}
