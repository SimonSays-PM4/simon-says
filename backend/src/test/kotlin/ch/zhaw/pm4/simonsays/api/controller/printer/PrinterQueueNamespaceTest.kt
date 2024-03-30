package ch.zhaw.pm4.simonsays.api.controller.printer

import ch.zhaw.pm4.simonsays.testutils.mockSocket
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class PrinterQueueNamespaceTest {
    private lateinit var namespace: PrinterQueueNamespace

    @BeforeEach
    fun setUp() {
        namespace = PrinterQueueNamespace()
    }

    @ParameterizedTest
    @CsvSource(
        "/socket-api/v1/printer-server/12345/print-queues, 12345, ", // Numeric id, no queue id
        "/socket-api/v1/printer-server/abc123/print-queues, abc123, ", // Alphanumeric id, no queue id
        "/socket-api/v1/printer-server/4f52b1a0-c7b7-4d20-8a2e-5a839092d9c4/print-queues, 4f52b1a0-c7b7-4d20-8a2e-5a839092d9c4, ", // UUID v4, no queue id
        "/socket-api/v1/printer-server/4f52b1a0-c7b7-4d20-8a2e-5a839092d9c4/print-queues/8df0b909-182b-4ec6-a085-9f4dc0d3ff1f, 4f52b1a0-c7b7-4d20-8a2e-5a839092d9c4, 8df0b909-182b-4ec6-a085-9f4dc0d3ff1f", // UUID v4 printer server id, UUID v4 queid
    )
    fun `test namespace pattern matching with valid inputs`(
        input: String, expectedPrinterServerId: String, expectedPrintQueueId: String?
    ) {
        val matchResult = PrinterQueueNamespace.namespacePattern.matchEntire(input)
        assertNotNull(matchResult, "Expected a match for input: $input")
        assertEquals(expectedPrinterServerId, matchResult!!.groups[1]?.value)
        assertEquals(expectedPrintQueueId, matchResult.groups[2]?.value)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["/socket-api/v1/printer-server//print-queues", // Missing printer server id
            "/invalid-input", // Completely invalid format
            "/socket-api/v1/printer-server//print-queues/1234" // Missing printer server id but present print queue id
        ]
    )
    fun `test namespace pattern matching with invalid inputs`(input: String) {
        val matchResult = PrinterQueueNamespace.namespacePattern.matchEntire(input)
        assertNull(matchResult, "Expected no match for input: $input")
    }

    @Test
    fun `onConnection should add socket to subscribersToAllPrintQueues if printQueueID is null`() {
        val printerServerId = "test-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-server/$printerServerId/print-queues")
        namespace.onConnection(mockSocket)

        assert(namespace.subscribersToAllPrintQueues[printerServerId]?.contains(mockSocket) ?: false)
    }

    @Test
    fun `onConnection should add socket to subscribersToSpecificPrintQueues if printQueueID is provided`() {
        val printerServerId = "test-printer-server-id"
        val printQueueId = "test-print-queue-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-server/$printerServerId/print-queues/$printQueueId")

        namespace.onConnection(mockSocket)

        assert(
            namespace.subscribersToSpecificPrintQueues[printerServerId]?.get(printQueueId)?.contains(mockSocket)
                ?: false
        )
    }

    @Test
    fun `onDisconnection should remove socket from subscribersToAllPrintQueues if printQueueID is null`() {
        val printerServerId = "test-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-server/$printerServerId/print-queues")
        namespace.onConnection(mockSocket) // First, connect the socket
        namespace.onDisconnection(mockSocket) // Then, disconnect it

        assert(namespace.subscribersToAllPrintQueues[printerServerId]?.isEmpty() ?: true)
    }

    @Test
    fun `onDisconnection should remove socket from subscribersToSpecificPrintQueues if printQueueID is provided`() {
        val printerServerId = "test-printer-server-id"
        val printQueueId = "test-print-queue-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-server/$printerServerId/print-queues/$printQueueId")

        namespace.onConnection(mockSocket) // First, connect the socket
        namespace.onDisconnection(mockSocket) // Then, disconnect it

        assert(namespace.subscribersToSpecificPrintQueues[printerServerId]?.get(printQueueId)?.isEmpty() ?: true)
    }
}