package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.controller.SocketIoNamespace
import ch.zhaw.pm4.simonsays.api.controller.StationViewNamespace
import ch.zhaw.pm4.simonsays.api.controller.printer.PrinterServersNamespace
import ch.zhaw.pm4.simonsays.api.mapper.OrderMapperImpl
import ch.zhaw.pm4.simonsays.api.types.OrderIngredientDTO
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import ch.zhaw.pm4.simonsays.repository.OrderIngredientRepository
import ch.zhaw.pm4.simonsays.repository.StationRepository
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
    protected lateinit var ingredientRepository: IngredientRepository

    @MockkBean(relaxed = true)
    protected lateinit var orderIngredientRepository: OrderIngredientRepository

    @MockkBean(relaxed = true)
    protected lateinit var eventRepository: EventRepository

    @BeforeEach
    fun setup() {
        stationRepository = mockk(relaxed = true)
        ingredientRepository = mockk(relaxed = true)
        orderIngredientRepository = mockk(relaxed = true)
        eventRepository = mockk(relaxed = true)
        stationViewNamespace = StationViewNamespace(
                stationRepository,
                OrderMapperImpl(),
                ingredientRepository,
                orderIngredientRepository,
                eventRepository
        )
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
        val matchResult = PrinterServersNamespace.namespacePattern.matchEntire(input)
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

        Assertions.assertTrue(stationViewNamespace.subscribeToAssemblyStationEvents.contains(mockSocket))
    }

    @Test
    fun `onDisconnection should remove socket from subscribersToAllPrinterServers if print server id is not provided`() {
        val eventId: Long = 123456
        val stationId: Long = 654321
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/view/${stationId}")
        stationViewNamespace.onConnection(mockSocket) // First, connect the socket
        stationViewNamespace.onDisconnect(mockSocket) // Then, disconnect it

        Assertions.assertTrue(stationViewNamespace.subscribeToAssemblyStationEvents.isEmpty())
    }

    @Test
    fun `onRemove should send remove event to all subscribers`() {
        val eventId: Long = 123456
        val stationId: Long = 654321
        val mockSocket = mockSocket("/socket-api/v1/event/${eventId}/station/view/${stationId}")
        val mockOrderIngredientDTO = mockk<OrderIngredientDTO>(relaxed = true)

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

        stationViewNamespace.onConnection(mockSocket) // First, connect the socket
        stationViewNamespace.onChange(mockOrderIngredientDTO) // Then, change it

        verify { mockSocket.send(SocketIoNamespace.CHANGE_EVENT, any()) }
    }

}