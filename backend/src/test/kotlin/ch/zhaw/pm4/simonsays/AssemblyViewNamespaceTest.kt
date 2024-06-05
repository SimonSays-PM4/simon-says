package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.controller.AssemblyViewNamespace
import ch.zhaw.pm4.simonsays.api.controller.socketio.SocketIoNamespace
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.repository.OrderRepository
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

    @MockkBean(relaxed = true)
    protected lateinit var orderRepository: OrderRepository

    @BeforeEach
    fun setup() {
        eventRepository = mockk(relaxed = true)
        stationService = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        assemblyViewNamespace = AssemblyViewNamespace(
                eventRepository,
                stationService,
                orderRepository
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
        val matchResult = AssemblyViewNamespace.namespacePattern.matchEntire(input)
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

        Assertions.assertTrue(assemblyViewNamespace.subscribeToAssemblyStationEvents[eventId]!!.contains(mockSocket))
    }

    @Test
    fun `onConnection should handle error and send application error`() {
        val eventId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/assembly")

        every {
            stationService.getAssemblyStationView(any())
        } throws Exception("Error")

        assemblyViewNamespace.onConnection(mockSocket)

        verify { mockSocket.send(eq("application-error"), any()) }
    }

    @Test
    fun `onApplicationError by id should send errors to subscribed sockets`() {
        val eventId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/assembly")

        assemblyViewNamespace.onConnection(mockSocket)
        assemblyViewNamespace.onApplicationError(eventId, "some-error", "some more explicit error message")

        verify { mockSocket.send(eq("application-error"), any()) }
    }

    @Test
    fun `onApplicationError by id should not send errors to other event sockets`() {
        val eventId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/assembly")

        assemblyViewNamespace.onConnection(mockSocket)
        assemblyViewNamespace.onApplicationError(2, "some-error", "some more explicit error message")

        verify(exactly = 0) { mockSocket.send(eq("application-error"), any()) }
    }

    @Test
    fun `onApplicationError by id without id should send error to all sockets`() {
        val eventId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/assembly")

        assemblyViewNamespace.onConnection(mockSocket)
        assemblyViewNamespace.onApplicationError(null, "some-error", "some more explicit error message")

        verify { mockSocket.send(eq("application-error"), any()) }
    }

    @Test
    fun `onDisconnection should remove socket from subscribeToAssemblyStationEvents if print server id is not provided`() {
        val eventId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/assembly")
        assemblyViewNamespace.onConnection(mockSocket) // First, connect the socket
        assemblyViewNamespace.onDisconnect(mockSocket) // Then, disconnect it

        Assertions.assertTrue(assemblyViewNamespace.subscribeToAssemblyStationEvents[eventId]!!.isEmpty())
    }

    @Test
    fun `onRemove should send remove event to all subscribers`() {
        val eventId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/assembly")
        val mockOrder = getOrderDTO(1)

        every {
            orderRepository.findById(any())
        } returns Optional.of(getOrder())

        assemblyViewNamespace.onConnection(mockSocket) // First, connect the socket
        assemblyViewNamespace.onRemove(mockOrder) // Then, remove it

        verify { mockSocket.send(SocketIoNamespace.REMOVE_EVENT, any()) }
    }

    @Test
    fun `onChange should send change event to all subscribers`() {
        val eventId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/assembly")
        val mockOrder = getOrderDTO(1)

        every {
            orderRepository.findById(any())
        } returns Optional.of(getOrder())

        assemblyViewNamespace.onConnection(mockSocket) // First, connect the socket
        assemblyViewNamespace.onChange(mockOrder) // Then, remove it

        verify { mockSocket.send(SocketIoNamespace.CHANGE_EVENT, any()) }
    }

    @Test
    fun `onChange should only notify all sockets in correct namespace`() {
        val eventId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/assembly")
        val mockSocket2 = mockSocket("/socket-api/v1/event/${eventId}/station/assembly")
        val mockSocket3 = mockSocket("/socket-api/v1/event/${eventId + 1}/station/assembly")
        val mockOrderDTO = getOrderDTO()

        every {
            orderRepository.findById(any())
        } returns Optional.of(getOrder())

        assemblyViewNamespace.onConnection(mockSocket)
        assemblyViewNamespace.onConnection(mockSocket2)
        assemblyViewNamespace.onConnection(mockSocket3)
        assemblyViewNamespace.onChange(mockOrderDTO)

        verify(exactly = 2) { mockSocket.send(any(), any()) }
        verify(exactly = 2) { mockSocket2.send(any(), any()) }
        verify(exactly = 1) { mockSocket3.send(any(), any()) }

    }

    @Test
    fun `test onChange with invalid order id throws exception`() {
        every {
            orderRepository.findById(any())
        } returns Optional.empty()
        assertThrows<ResourceNotFoundException> {
            assemblyViewNamespace.onChange(getOrderDTO())
        }
    }
}