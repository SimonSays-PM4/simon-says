package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.controller.socketio.SocketIoNamespace
import ch.zhaw.pm4.simonsays.api.controller.StationViewNamespace
import ch.zhaw.pm4.simonsays.api.types.OrderIngredientDTO
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.repository.StationRepository
import ch.zhaw.pm4.simonsays.service.IngredientService
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

class StationViewNamespaceTest {

    protected lateinit var stationViewNamespace: StationViewNamespace

    @MockkBean(relaxed = true)
    protected lateinit var stationRepository: StationRepository

    @MockkBean(relaxed = true)
    protected lateinit var eventRepository: EventRepository

    @MockkBean(relaxed = true)
    protected lateinit var stationService: StationService

    @MockkBean(relaxed = true)
    protected lateinit var ingredientService: IngredientService

    @BeforeEach
    fun setup() {
        stationRepository = mockk(relaxed = true)
        eventRepository = mockk(relaxed = true)
        stationService = mockk(relaxed = true)
        ingredientService = mockk(relaxed = true)
        stationViewNamespace = StationViewNamespace(
                stationRepository,
                eventRepository,
                stationService,
                ingredientService
        )
    }

    @Test
    fun `test namespace matches for correct input`() {
        val nameSpace = "/socket-api/v1/event/2/station/view/4"
        Assertions.assertTrue(stationViewNamespace.isPartOfNamespace(nameSpace))
    }

    @ParameterizedTest
    @CsvSource(
            "/socket-api/v1/event/2/station/view/4, 2, 4",
            "/socket-api/v1/event/7/station/view/16, 7, 16"
    )
    fun `test namespace pattern matching with valid inputs`(
            input: String,
            expectedEventId: Long,
            expectedStationId: Long
    ) {
        val matchResult = StationViewNamespace.namespacePattern.matchEntire(input)
        Assertions.assertNotNull(matchResult, "Expected a match for input: $input")
        Assertions.assertEquals(expectedEventId, (matchResult!!.groups[1]?.value)!!.toLong())
        Assertions.assertEquals(expectedStationId, (matchResult.groups[2]?.value)!!.toLong())
    }

    @ParameterizedTest
    @ValueSource(
            strings = ["/socket-api/v1/event//station/view/4", // Missing event Id
                "/invalid-input", // Completely invalid format
            ]
    )
    fun `test namespace pattern matching with invalid inputs`(input: String) {
        val matchResult = StationViewNamespace.namespacePattern.matchEntire(input)
        Assertions.assertNull(matchResult, "Expected no match for input: $input")
    }

    @Test
    fun `test isPartOfNamespace with valid but non-existing namespace (invalid event)`() {
        val eventId: Long = 123456
        val stationId: Long = 654321
        val namespace = "/socket-api/v1/event/${eventId}/station/view/${stationId}"
        every {
            eventRepository.findById(any())
        } returns Optional.empty()
        assertThrows<ResourceNotFoundException> {
            stationViewNamespace.isPartOfNamespace(namespace)
        }
    }

    @Test
    fun `test isPartOfNamespace with valid but non-existing namespace (invalid station)`() {
        val eventId: Long = 123456
        val stationId: Long = 654321
        val namespace = "/socket-api/v1/event/${eventId}/station/view/${stationId}"
        every {
            eventRepository.findById(any())
        } returns Optional.of(
                getEvent()
        )
        every {
            stationRepository.findByIdAndEventId(any(), any())
        } returns Optional.empty()
        assertThrows<ResourceNotFoundException> {
            stationViewNamespace.isPartOfNamespace(namespace)
        }
    }

    @Test
    fun `onConnection should add socket to subscribeToAssemblyStationEvents if the eventId & stationId is valid`() {
        val eventId: Long = 123456
        val stationId: Long = 654321
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/view/${stationId}")

        stationViewNamespace.onConnection(mockSocket)

        Assertions.assertTrue(stationViewNamespace.subscribeToSpecificStation[Pair(eventId, stationId)]!!.contains(mockSocket))
    }

    @Test
    fun `onConnection should handle error and call onApplicationError`() {
        val eventId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/assembly")

        every {
            stationService.getAssemblyStationView(any())
        } throws Exception("Error")

        stationViewNamespace.onConnection(mockSocket)

        verify { mockSocket.send(eq("application-error"), any()) }
    }

    @Test
    fun `onDisconnection should remove socket from subscribeToSpecificStation`() {
        val eventId: Long = 123456
        val stationId: Long = 654321
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/view/${stationId}")
        stationViewNamespace.onConnection(mockSocket) // First, connect the socket
        stationViewNamespace.onDisconnect(mockSocket) // Then, disconnect it

        Assertions.assertTrue(stationViewNamespace.subscribeToSpecificStation[Pair(eventId, stationId)]!!.isEmpty())
    }

    @Test
    fun `onRemove should send remove event to all subscribers`() {
        val eventId: Long = 123456
        val stationId: Long = 654321
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/view/${stationId}")
        val mockOrderIngredientDTO = mockk<OrderIngredientDTO>(relaxed = true)

        every { stationService.getStationAssociatedWithIngredient(any()) } returns ( listOf(getStation(id = stationId, event = getEvent(id = eventId))) )

        stationViewNamespace.onConnection(mockSocket) // First, connect the socket
        stationViewNamespace.onRemove(mockOrderIngredientDTO) // Then, remove it

        verify { mockSocket.send(SocketIoNamespace.REMOVE_EVENT, any()) }
    }

    @Test
    fun `onChange should send change event to all subscribers`() {
        val eventId: Long = 123456
        val stationId: Long = 654321
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/view/${stationId}")
        val mockOrderIngredientDTO = mockk<OrderIngredientDTO>(relaxed = true)

        every { stationService.getStationAssociatedWithIngredient(any()) } returns ( listOf(getStation(id = stationId, event = getEvent(id = eventId))) )

        stationViewNamespace.onConnection(mockSocket) // First, connect the socket
        stationViewNamespace.onChange(mockOrderIngredientDTO) // Then, change it

        verify { mockSocket.send(SocketIoNamespace.CHANGE_EVENT, any()) }
    }

    @Test
    fun `onChange should only notify all sockets in correct namespace`() {
        val eventId: Long = 123456
        val stationId: Long = 654321
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/view/${stationId}")
        val mockSocket2 = mockSocket("/socket-api/v1/event/${eventId}/station/view/${stationId}")
        val mockSocket3 = mockSocket("/socket-api/v1/event/${eventId}/station/view/${stationId + 1}")
        val mockOrderIngredientDTO = mockk<OrderIngredientDTO>(relaxed = true)

        every { stationService.getStationAssociatedWithIngredient(any()) } returns ( listOf(getStation(id = stationId, event = getEvent(id = eventId))) )

        stationViewNamespace.onConnection(mockSocket)
        stationViewNamespace.onConnection(mockSocket2)
        stationViewNamespace.onConnection(mockSocket3)
        stationViewNamespace.onChange(mockOrderIngredientDTO)

        verify(exactly = 2) { mockSocket.send(any(), any()) }
        verify(exactly = 2) { mockSocket2.send(any(), any()) }
        verify(exactly = 1) { mockSocket3.send(any(), any()) }

    }

    @Test
    fun `onApplicationError by id should send errors to subscribed sockets`() {
        val eventId: Long = 1
        val stationId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/view/${stationId}")

        stationViewNamespace.onConnection(mockSocket)
        stationViewNamespace.onApplicationError(Pair(eventId, stationId), "some-error", "some more explicit error message")

        verify { mockSocket.send(eq("application-error"), any()) }
    }

    @Test
    fun `onApplicationError by id should not send errors to other event sockets`() {
        val eventId: Long = 1
        val stationId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/view/${stationId}")

        every { stationService.getStationView(any(), any()) } returns ( listOf(getOrderIngredientDTO()) )

        stationViewNamespace.onConnection(mockSocket)
        stationViewNamespace.onApplicationError(Pair(2, stationId), "some-error", "some more explicit error message")

        verify(exactly = 0) { mockSocket.send(eq("application-error"), any()) }
    }

    @Test
    fun `onApplicationError by id without id should send error to all sockets`() {
        val eventId: Long = 1
        val stationId: Long = 1
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/view/${stationId}")

        stationViewNamespace.onConnection(mockSocket)
        stationViewNamespace.onApplicationError(null, "some-error", "some more explicit error message")

        verify { mockSocket.send(eq("application-error"), any()) }
    }

}