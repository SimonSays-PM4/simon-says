package ch.zhaw.pm4.simonsays.api.controller.printer

import ch.zhaw.pm4.simonsays.api.controller.SocketIoNamespace.Companion.APPLICATION_ERROR_EVENT
import ch.zhaw.pm4.simonsays.api.controller.SocketIoNamespace.Companion.CHANGE_EVENT
import ch.zhaw.pm4.simonsays.api.controller.SocketIoNamespace.Companion.INITIAL_DATA_EVENT
import ch.zhaw.pm4.simonsays.api.controller.SocketIoNamespace.Companion.REMOVE_EVENT
import ch.zhaw.pm4.simonsays.api.types.printer.JobStatusDto
import ch.zhaw.pm4.simonsays.api.types.printer.PrintQueueJobDto
import ch.zhaw.pm4.simonsays.service.printer.PrintQueueJobService
import ch.zhaw.pm4.simonsays.service.printer.PrintQueueService
import ch.zhaw.pm4.simonsays.service.printer.PrinterServerService
import ch.zhaw.pm4.simonsays.testutils.testObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.socket.socketio.server.SocketIoNamespace
import io.socket.socketio.server.SocketIoSocket
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class PrintQueueJobsNamespaceTest {
    private val mockPrinterServerService = mockk<PrinterServerService>(relaxed = true)
    private val mockPrintQueueService = mockk<PrintQueueService>(relaxed = true)
    private val mockPrintQueueJobService = mockk<PrintQueueJobService>(relaxed = true)
    private val objectMapper = testObjectMapper()
    private lateinit var namespace: PrintQueueJobsNamespace

    @BeforeEach
    fun setUp() {
        namespace = PrintQueueJobsNamespace(
            mockPrinterServerService,
            mockPrintQueueService,
            mockPrintQueueJobService,
            objectMapper
        )
    }

    @ParameterizedTest
    @CsvSource(
        "/socket-api/v1/printer-servers/wxyz/print-queues/abcd/jobs, wxyz, abcd, ", // Both IDs without job id
        "/socket-api/v1/printer-servers/1234/print-queues/5678/jobs/9012, 1234, 5678, 9012", // Numeric ids including job id
        "/socket-api/v1/printer-servers/abc123/print-queues/def456/jobs/ghi789, abc123, def456, ghi789", // Alphanumeric ids
        "/socket-api/v1/printer-servers/1a2b3c4d-5e6f-7g8h-9i0j-k1l2m3n4o5p6/print-queues/7g8h9i0j-1k2l-3m4n-5o6p-7q8r9s0t/jobs/a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4, 1a2b3c4d-5e6f-7g8h-9i0j-k1l2m3n4o5p6, 7g8h9i0j-1k2l-3m4n-5o6p-7q8r9s0t, a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4", // UUIDs for all ids
        "/socket-api/v1/printer-servers/1a2b3c4d-5e6f-7g8h-9i0j-k1l2m3n4o5p6/print-queues/7g8h9i0j-1k2l-3m4n-5o6p-7q8r9s0t/jobs/next, 1a2b3c4d-5e6f-7g8h-9i0j-k1l2m3n4o5p6, 7g8h9i0j-1k2l-3m4n-5o6p-7q8r9s0t, next" // Match next
    )
    fun `test print queue jobs namespace pattern matching with valid inputs`(
        input: String, expectedPrinterServerId: String?, expectedPrintQueueId: String?, expectedJobId: String?
    ) {
        val matchResult = PrintQueueJobsNamespace.namespacePattern.matchEntire(input)
        assertNotNull(matchResult, "Expected a match for input: $input")
        assertEquals(expectedPrinterServerId, matchResult!!.groups[1]?.value)
        assertEquals(expectedPrintQueueId, matchResult.groups[2]?.value)
        assertEquals(expectedJobId, matchResult.groups[3]?.value)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "/socket-api/v1/printer-servers/wxyz/print-queues/", // Missing print queue id and job id
            "/socket-api/v1/printer-servers/wxyz/jobs/123", // Incorrect path, missing print queue id
            "/invalid-api/v1/printer-servers/1234/print-queues/5678/jobs", // Invalid base path
            "/socket-api/v1/printer-servers/1234/print-queues//jobs/789", // Missing print queue id
            "/socket-api/v1/printer-servers//print-queues/abcd/jobs/efgh" // Missing printer server id
        ]
    )
    fun `test print queue jobs namespace pattern matching with invalid inputs`(input: String) {
        val matchResult = PrintQueueJobsNamespace.namespacePattern.matchEntire(input)
        assertNull(matchResult, "Expected no match for input: $input")
    }

    @Test
    fun `test isPartOfNamespace with valid and existing namespace`() {
        val printerServerId = "server1"
        val printQueueId = "queue1"
        val jobId = "job1"
        val namespaceString = "/socket-api/v1/printer-servers/$printerServerId/print-queues/$printQueueId/jobs/$jobId"
        every { mockPrinterServerService.doesPrinterServerExist(printerServerId) } returns true
        every { mockPrintQueueService.doesPrintQueueExist(printQueueId) } returns true
        every { mockPrintQueueJobService.doesPrintQueueJobExist(jobId) } returns true
        assertTrue(namespace.isPartOfNamespace(namespaceString))
    }

    @Test
    fun `test isPartOfNamespace with invalid namespace`() {
        val namespaceString = "/wrong-namespace"
        assertFalse(namespace.isPartOfNamespace(namespaceString))
    }

    @Test
    fun `onConnection should subscribe to specific job and send initial data`() {
        val printerServerId = "server1"
        val printQueueId = "queue1"
        val jobId = "job1"
        val socket =
            mockSocket("/socket-api/v1/printer-servers/$printerServerId/print-queues/$printQueueId/jobs/$jobId")
        val jobDto: PrintQueueJobDto = mockk(relaxed = true)

        every { mockPrintQueueJobService.getPrintQueueJobById(jobId) } returns jobDto

        namespace.onConnection(socket)

        verify { socket.send(INITIAL_DATA_EVENT, any()) }
        assertTrue(namespace.subscribersToSpecificPrintQueueJobs[jobId]?.contains(socket) ?: false)
    }

    @Test
    fun `onChangeEventReceived should update job and notify subscribers`() {
        val printerServerId = "server1"
        val printQueueId = "queue1"
        val jobId = "job1"
        val socket =
            mockSocket("/socket-api/v1/printer-servers/$printerServerId/print-queues/$printQueueId/jobs/$jobId")
        val initialJobDto: PrintQueueJobDto = mockk(relaxed = true)
        every { initialJobDto.id } returns jobId
        val updatedJobJson = JSONObject("""{"id":"$jobId","status":"PRINTED"}""")
        val updatedJobDto: PrintQueueJobDto = mockk(relaxed = true)
        every { updatedJobDto.id } returns jobId
        every { updatedJobDto.status } returns JobStatusDto.PRINTED

        every { mockPrintQueueJobService.getPrintQueueJobById(jobId) } returns initialJobDto
        every { mockPrintQueueJobService.doesPrintQueueJobExist(jobId) } returns true
        every { mockPrintQueueJobService.savePrintQueueJob(printQueueId, any()) } returns updatedJobDto

        namespace.onConnection(socket) // Connect to set up the subscriber
        namespace.onChangeEventReceived(arrayOf(updatedJobJson), printQueueId, jobId, socket)

        verify { socket.send(CHANGE_EVENT, any()) }
    }

    @Test
    fun `onRemoveEventReceived should remove job and notify subscribers`() {
        val printerServerId = "server1"
        val printQueueId = "queue1"
        val jobId = "job1"
        val socket =
            mockSocket("/socket-api/v1/printer-servers/$printerServerId/print-queues/$printQueueId/jobs/$jobId")
        val jobDto: PrintQueueJobDto = mockk(relaxed = true)
        every { jobDto.id } returns jobId
        every { mockPrintQueueJobService.getPrintQueueJobById(jobId) } returns jobDto

        namespace.onConnection(socket) // Connect to set up the subscriber
        namespace.onRemoveEventReceived(arrayOf(JSONObject("""{"id":"$jobId"}""")), jobId, socket)

        verify { socket.send(REMOVE_EVENT, any()) }
        verify { mockPrintQueueJobService.removePrintQueueJob(jobId) }
    }

    @Test
    fun `onChangeEventReceived with invalid data should emit error`() {
        val socket = mockSocket("/socket-api/v1/printer-servers/server1/print-queues/queue1/jobs/job1")
        namespace.onConnection(socket)
        namespace.onChangeEventReceived(arrayOf("Invalid Data"), "queue1", "job1", socket)

        verify { socket.send(APPLICATION_ERROR_EVENT, any()) }
    }

    @Test
    fun `onConnection should subscribe to next job and send initial data`() {
        val printerServerId = "server1"
        val printQueueId = "queue1"
        val namespaceString = "/socket-api/v1/printer-servers/$printerServerId/print-queues/$printQueueId/jobs/next"
        val socket = mockSocket(namespaceString)
        val nextJobDto: PrintQueueJobDto = mockk(relaxed = true)
        every { nextJobDto.id } returns UUID.randomUUID().toString()
        every { nextJobDto.status } returns JobStatusDto.PENDING

        every { mockPrintQueueJobService.getNextPendingPrintQueueJob(printQueueId) } returns nextJobDto

        namespace.onConnection(socket)

        verify { socket.send(INITIAL_DATA_EVENT, any()) }
        assertTrue(namespace.subscribersToNextPrintQueueJob[printQueueId]?.contains(socket) ?: false)
    }

    private fun mockSocket(namespaceString: String): SocketIoSocket {
        val socket = mockk<SocketIoSocket>(relaxed = true)
        val namespace = mockk<SocketIoNamespace>()
        every { namespace.name } returns namespaceString
        every { socket.namespace } returns namespace
        return socket
    }
}