package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.controller.AssemblyViewNamespace
import ch.zhaw.pm4.simonsays.api.controller.SocketIoNamespace
import ch.zhaw.pm4.simonsays.api.controller.printer.PrinterServersNamespace
import ch.zhaw.pm4.simonsays.api.types.OrderDTO
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.service.StationService
import ch.zhaw.pm4.simonsays.testutils.mockSocket
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class AssemblyViewNamespaceTest {

    protected lateinit var assemblyViewNamespace: AssemblyViewNamespace

    @MockkBean(relaxed = true)
    protected lateinit var eventRepository: EventRepository

    @MockkBean(relaxed = true)
    protected lateinit var stationService: StationService

    @BeforeEach
    fun setup() {
        eventRepository = mockk(relaxed = true)
        stationService = mockk(relaxed = true)
        assemblyViewNamespace = AssemblyViewNamespace(
                eventRepository,
                stationService
        )
    }

    @ParameterizedTest
    @CsvSource(
            "/socket-api/v1/event/1/station/assembly, 1"
    )
    fun `test namespace pattern matching with valid inputs`(
            input: String,
            expectedEventId: String?,
    ) {
        val matchResult = AssemblyViewNamespace.namespacePattern.matchEntire(input)
        Assertions.assertNotNull(matchResult, "Expected a match for input: $input")
        Assertions.assertEquals(expectedEventId, matchResult!!.groups[1]?.value)
    }

    @ParameterizedTest
    @ValueSource(
            strings = ["/socket-api/v1/event//station/assembly", // Missing event Id
                "/invalid-input", // Completely invalid format
            ]
    )
    fun `test namespace pattern matching with invalid inputs`(input: String) {
        val matchResult = PrinterServersNamespace.namespacePattern.matchEntire(input)
        Assertions.assertNull(matchResult, "Expected no match for input: $input")
    }

    @Test
    fun `test isPartOfNamespace with valid but non-existing namespace (invalid event)`() {
        val eventId: Long = 123456
        val namespace = "/socket-api/v1/event/${eventId}/station/assembly"
        every {
            eventRepository.findById(eventId)
        } returns Optional.empty()
        assertThrows<ResourceNotFoundException> {
            assemblyViewNamespace.isPartOfNamespace(namespace)
        }
    }

    @Test
    fun `test isPartOfNamespace with valid but non-existing namespace (no assembly station)`() {
        val eventId: Long = 123456
        val namespace = "/socket-api/v1/event/${eventId}/station/assembly"
        every {
            stationService.doesEventHaveAssemblyStation(any())
        } throws ResourceNotFoundException("The event with id: $eventId does not yet have an assembly station")
        assertThrows<ResourceNotFoundException> {
            assemblyViewNamespace.isPartOfNamespace(namespace)
        }
    }

    @Test
    fun `onConnection should add socket to subscribeToAssemblyStationEvents if the eventId is valid`() {
        val eventId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/assembly")

        assemblyViewNamespace.onConnection(mockSocket)

        Assertions.assertTrue(assemblyViewNamespace.subscribeToAssemblyStationEvents.contains(mockSocket))
    }

    @Test
    fun `onDisconnection should remove socket from subscribersToAllPrinterServers if print server id is not provided`() {
        val eventId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/assembly")
        assemblyViewNamespace.onConnection(mockSocket) // First, connect the socket
        assemblyViewNamespace.onDisconnect(mockSocket) // Then, disconnect it

        Assertions.assertTrue(assemblyViewNamespace.subscribeToAssemblyStationEvents.isEmpty())
    }

    @Test
    fun `onRemove should send remove event to all subscribers`() {
        val eventId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/assembly")
        val mockOrder = mockk<OrderDTO>(relaxed = true)

        assemblyViewNamespace.onConnection(mockSocket) // First, connect the socket
        assemblyViewNamespace.onRemove(mockOrder) // Then, remove it

        verify { mockSocket.send(SocketIoNamespace.REMOVE_EVENT, any()) }
    }

    @Test
    fun `onChange should send change event to all subscribers`() {
        val eventId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/assembly")
        val mockOrder = mockk<OrderDTO>(relaxed = true)

        assemblyViewNamespace.onConnection(mockSocket) // First, connect the socket
        assemblyViewNamespace.onChange(mockOrder) // Then, remove it

        verify { mockSocket.send(SocketIoNamespace.CHANGE_EVENT, any()) }
    }
}