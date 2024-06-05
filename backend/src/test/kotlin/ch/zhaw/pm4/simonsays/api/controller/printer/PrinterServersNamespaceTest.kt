package ch.zhaw.pm4.simonsays.api.controller.printer

import ch.zhaw.pm4.simonsays.api.controller.socketio.SocketIoNamespace.Companion.APPLICATION_ERROR_EVENT
import ch.zhaw.pm4.simonsays.api.controller.socketio.SocketIoNamespace.Companion.CHANGE_EVENT
import ch.zhaw.pm4.simonsays.api.controller.socketio.SocketIoNamespace.Companion.REMOVE_EVENT
import ch.zhaw.pm4.simonsays.api.types.printer.PrinterServerDTO
import ch.zhaw.pm4.simonsays.service.printer.PrinterServerService
import ch.zhaw.pm4.simonsays.testutils.mockSocket
import ch.zhaw.pm4.simonsays.testutils.testObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class PrinterServersNamespaceTest {
    private val objectMapper = testObjectMapper()
    private lateinit var mockPrinterServerService: PrinterServerService
    private lateinit var namespace: PrinterServersNamespace

    @BeforeEach
    fun setUp() {
        mockPrinterServerService = mockk(relaxed = true)
        namespace = PrinterServersNamespace(mockPrinterServerService, objectMapper)
    }

    @ParameterizedTest
    @CsvSource(
        "/socket-api/v1/printer-servers, ", // No id
        "/socket-api/v1/printer-servers/12345, 12345", // Numeric id
        "/socket-api/v1/printer-servers/abc123, abc123", // Alphanumeric id
        "/socket-api/v1/printer-servers/4f52b1a0-c7b7-4d20-8a2e-5a839092d9c4, 4f52b1a0-c7b7-4d20-8a2e-5a839092d9c4", // UUID v4
    )
    fun `test namespace pattern matching with valid inputs`(
        input: String,
        expectedPrinterServerId: String?,
    ) {
        val matchResult = PrinterServersNamespace.namespacePattern.matchEntire(input)
        assertNotNull(matchResult, "Expected a match for input: $input")
        assertEquals(expectedPrinterServerId, matchResult!!.groups[1]?.value)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["/socket-api/v1/printer-servers/", // Missing printer server id
            "/invalid-input", // Completely invalid format
            "/socket-api/v1/printer-servers//print-queues/1234" // Missing printer server id but present print queue id
        ]
    )
    fun `test namespace pattern matching with invalid inputs`(input: String) {
        val matchResult = PrinterServersNamespace.namespacePattern.matchEntire(input)
        assertNull(matchResult, "Expected no match for input: $input")
    }

    @Test
    fun `test isPartOfNamespace with valid and existing namespace`() {
        val printerServerId = "403fa706-31a4-4952-a403-d6b40c9f879a"
        val namespace = "/socket-api/v1/printer-servers/$printerServerId"
        every {
            mockPrinterServerService.doesPrinterServerExist(printerServerId)
        } returns true
        assertTrue(this.namespace.isPartOfNamespace(namespace))
    }

    @Test
    fun `test isPartOfNamespace with valid but non-existing namespace`() {
        val printerServerId = "403fa706-31a4-4952-a403-d6b40c9f879a"
        val namespace = "/socket-api/v1/printer-servers/$printerServerId"
        every {
            mockPrinterServerService.doesPrinterServerExist(printerServerId)
        } returns false
        assertFalse(this.namespace.isPartOfNamespace(namespace))
    }

    @Test
    fun `onConnection should add socket to subscribersToSpecificPrinterServer if print server id is provided`() {
        val printerServerId = "test-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-servers/$printerServerId")

        namespace.onConnection(mockSocket)

        assert(
            namespace.subscribersToSpecificPrinterServer[printerServerId]?.contains(mockSocket) ?: false
        )
    }

    @Test
    fun `onConnection should add socket to subscribersToAllPrinterServers if print server id is not provided`() {
        val mockSocket = mockSocket("/socket-api/v1/printer-servers")

        namespace.onConnection(mockSocket)

        assert(namespace.subscribersToAllPrinterServers.contains(mockSocket))
    }

    @Test
    fun `onDisconnection should remove socket from subscribersToSpecificPrinterServer if print server id is provided`() {
        val printerServerId = "test-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-servers/$printerServerId")
        namespace.onConnection(mockSocket) // First, connect the socket
        namespace.onDisconnect(mockSocket) // Then, disconnect it

        assert(namespace.subscribersToSpecificPrinterServer[printerServerId]?.isEmpty() ?: true)
    }

    @Test
    fun `onDisconnection should remove socket from subscribersToAllPrinterServers if print server id is not provided`() {
        val mockSocket = mockSocket("/socket-api/v1/printer-servers")
        namespace.onConnection(mockSocket) // First, connect the socket
        namespace.onDisconnect(mockSocket) // Then, disconnect it

        assert(namespace.subscribersToAllPrinterServers.isEmpty())
    }

    @Test
    fun `onRemove should send remove event to specific subscribers`() {
        val printerServerId = "test-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-servers/$printerServerId")
        val mockPrinterServer = mockk<PrinterServerDTO>(relaxed = true)
        every { mockPrinterServer.id } returns printerServerId

        namespace.onConnection(mockSocket) // First, connect the socket
        namespace.onRemove(mockPrinterServer) // Then, remove it

        verify { mockSocket.send(REMOVE_EVENT, any()) }
    }

    @Test
    fun `onRemove should send remove event to all subscribers`() {
        val printerServerId = "test-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-servers")
        val mockPrinterServer = mockk<PrinterServerDTO>(relaxed = true)
        every { mockPrinterServer.id } returns printerServerId

        namespace.onConnection(mockSocket) // First, connect the socket
        namespace.onRemove(mockPrinterServer) // Then, remove it

        verify { mockSocket.send(REMOVE_EVENT, any()) }
    }

    @Test
    fun `onChange should send change event to specific subscribers`() {
        val printerServerId = "test-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-servers/$printerServerId")
        val mockPrinterServer = mockk<PrinterServerDTO>(relaxed = true)
        every { mockPrinterServer.id } returns printerServerId

        namespace.onConnection(mockSocket) // First, connect the socket
        namespace.onChange(mockPrinterServer) // Then, change it

        verify { mockSocket.send(CHANGE_EVENT, any()) }
    }

    @Test
    fun `onChange should send change event to all subscribers`() {
        val printerServerId = "test-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-servers")
        val mockPrinterServer = mockk<PrinterServerDTO>(relaxed = true)
        every { mockPrinterServer.id } returns printerServerId

        namespace.onConnection(mockSocket) // First, connect the socket
        namespace.onChange(mockPrinterServer) // Then, change it

        verify { mockSocket.send(CHANGE_EVENT, any()) }
    }

    @Test
    fun `change event should update database and emited back`() {
        val subscribedPrinterId = "test-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-servers/$subscribedPrinterId")
        val printerServerDto = PrinterServerDTO(
            id = "test-printer-server-id",
            name = "test-printer-server-name",
            queues = emptyList(),
        )
        every { mockPrinterServerService.savePrinterServer(printerServerDto) } returns printerServerDto
        every { mockPrinterServerService.doesPrinterServerExist("test-printer-server-id") } returns true
        val printerServerDtoJson = arrayOf<Any>(JSONObject(printerServerDto))

        namespace.onConnection(mockSocket)
        namespace.onChangeEventReceived(printerServerDtoJson, subscribedPrinterId, mockSocket)

        verify { mockPrinterServerService.savePrinterServer(printerServerDto) }
        verify { mockSocket.send(CHANGE_EVENT, any()) }
    }

    @Test
    fun `change event with invalid data format should not update database`() {
        val subscribedPrinterId = "test-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-servers/$subscribedPrinterId")
        val invalidData = arrayOf<Any>("Not a JSON object")

        namespace.onConnection(mockSocket)
        namespace.onChangeEventReceived(invalidData, subscribedPrinterId, mockSocket)

        verify(inverse = true) { mockPrinterServerService.savePrinterServer(any()) }
        verify { mockSocket.send(APPLICATION_ERROR_EVENT, any()) }
    }

    @Test
    fun `change event for non-existing printer server should emit application error`() {
        val subscribedPrinterId = "non-existing-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-servers/$subscribedPrinterId")
        val printerServerDto = PrinterServerDTO(
            id = subscribedPrinterId,
            name = "Non-Existing Printer Server",
            queues = emptyList()
        )
        every { mockPrinterServerService.doesPrinterServerExist(subscribedPrinterId) } returns false
        val printerServerDtoJson = arrayOf<Any>(JSONObject(printerServerDto))

        namespace.onConnection(mockSocket)
        namespace.onChangeEventReceived(printerServerDtoJson, subscribedPrinterId, mockSocket)

        verify { mockSocket.send(APPLICATION_ERROR_EVENT, any()) }
    }

    @Test
    fun `change event for another printer server than subscribed should emit application error`() {
        val subscribedPrinterId = "subscribed-printer-server-id"
        val differentPrinterServerId = "different-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-servers/$subscribedPrinterId")
        val printerServerDto = PrinterServerDTO(
            id = differentPrinterServerId, // ID different from subscribed ID
            name = "Different Printer Server",
            queues = emptyList()
        )
        // Assume the printer server with a different ID does exist for this test case
        every { mockPrinterServerService.doesPrinterServerExist(differentPrinterServerId) } returns true
        val printerServerDtoJson = arrayOf<Any>(JSONObject(printerServerDto))

        namespace.onConnection(mockSocket)
        namespace.onChangeEventReceived(printerServerDtoJson, subscribedPrinterId, mockSocket)

        // Verify that the updatePrinterServer method is not called since the IDs do not match
        verify(inverse = true) { mockPrinterServerService.savePrinterServer(any()) }
        // Verify that an application error is emitted with a specific error code indicating the ID mismatch
        verify { mockSocket.send(APPLICATION_ERROR_EVENT, any()) }
    }


    @Test
    fun `remove event with missing printer server id should emit application error`() {
        val subscribedPrinterId = "test-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-servers/$subscribedPrinterId")
        val missingIdData = arrayOf<Any>(JSONObject())

        namespace.onConnection(mockSocket)
        namespace.onRemoveEventReceived(missingIdData, subscribedPrinterId, mockSocket)

        verify { mockSocket.send(APPLICATION_ERROR_EVENT, any()) }
    }

    @Test
    fun `remove event for non-existing printer server should emit application error`() {
        val subscribedPrinterId = "non-existing-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-servers/$subscribedPrinterId")
        every { mockPrinterServerService.getPrinterServerById(subscribedPrinterId) } returns null
        val printerServerDtoJson = arrayOf<Any>(JSONObject().put("id", subscribedPrinterId))

        namespace.onConnection(mockSocket)
        namespace.onRemoveEventReceived(printerServerDtoJson, subscribedPrinterId, mockSocket)

        verify { mockSocket.send(APPLICATION_ERROR_EVENT, any()) }
    }

    @Test
    fun `remove event for another printer server than subscribed should emit application error`() {
        val subscribedPrinterId = "subscribed-printer-server-id"
        val differentPrinterServerId = "different-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-servers/$subscribedPrinterId")
        val printerServerJson = JSONObject().put("id", differentPrinterServerId) // Different ID

        // Assume the printer server with the different ID exists for this test case
        every { mockPrinterServerService.getPrinterServerById(differentPrinterServerId) } returns PrinterServerDTO(
            id = differentPrinterServerId,
            name = "Different Printer Server Name",
            queues = emptyList()
        )

        namespace.onConnection(mockSocket)
        namespace.onRemoveEventReceived(arrayOf(printerServerJson), subscribedPrinterId, mockSocket)

        // Verify that the removePrinterServer method is not called since the IDs do not match
        verify(inverse = true) { mockPrinterServerService.removePrinterServer(differentPrinterServerId) }
        // Verify that an application error is emitted with a specific error code
        verify { mockSocket.send(APPLICATION_ERROR_EVENT, any()) }
    }

    @Test
    fun `successful removal of a subscribed printer server should notify client`() {
        val subscribedPrinterId = "subscribed-printer-server-id"
        val mockSocket = mockSocket("/socket-api/v1/printer-servers/$subscribedPrinterId")
        val printerServerJson = JSONObject().put("id", subscribedPrinterId)

        // Set up the service to return a specific printer server when queried
        every { mockPrinterServerService.getPrinterServerById(subscribedPrinterId) } returns PrinterServerDTO(
            id = subscribedPrinterId,
            name = "Subscribed Printer Server",
            queues = emptyList()
        )

        namespace.onConnection(mockSocket)
        namespace.onRemoveEventReceived(arrayOf(printerServerJson), subscribedPrinterId, mockSocket)

        // Verify that the removePrinterServer method is called with the correct ID
        verify { mockPrinterServerService.removePrinterServer(subscribedPrinterId) }
        // Verify that a remove event is sent back to the client with the correct data
        verify { mockSocket.send(REMOVE_EVENT, any()) }
    }
}