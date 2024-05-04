package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.mapper.EventMapper
import ch.zhaw.pm4.simonsays.api.mapper.EventMapperImpl
import ch.zhaw.pm4.simonsays.api.types.EventCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.service.EventService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import java.util.Optional.empty

class EventTest {
    @MockkBean(relaxed = true)
    protected lateinit var eventRepository: EventRepository

    private lateinit var eventService: EventService

    @BeforeEach
    fun setup() {
        // mockkStatic("kotlinx.coroutines.reactor.MonoKt")
        eventRepository = mockk(relaxed = true)
        eventService = EventService(eventRepository, EventMapperImpl())
    }

    @Test
    fun `Test event creation`() {
        every { eventRepository.save(any()) } returns Event(
                15,
            "Testevent",
                "Testeventpassword",
                1
        )
        val eventCreateUpdateDTO = EventCreateUpdateDTO(
                null,
                "Testevent",
                "Testeventpassword",
                15
        )
        Assertions.assertEquals(EventDTO(
                15,
                "Testevent",
                "Testeventpassword",
                1
        ), eventService.createUpdateEvent(eventCreateUpdateDTO))
    }

    @Test
    fun `Test event fetching`() {
        every { eventRepository.findAll() } returns mutableListOf(
                Event(
                    name = "testevent",
                    password = "password",
                    numberOfTables = 2

                ),
                Event(
                    name = "testevent2",
                    password = "password",
                    numberOfTables = 2
                )
        )
        val events: List<EventDTO> = eventService.getEvents()
        Assertions.assertEquals(2, events.count())
    }

    @Test
    fun `Test event get`() {
        every { eventRepository.findById(1) } returns Optional.of(Event(
                name = "testevent",
                password = "testeventpassword",
                numberOfTables = 1
        ))
        Assertions.assertEquals(
                EventDTO(
                        id = null,
                        name = "testevent",
                        password = "testeventpassword",
                        numberOfTables = 1,
                ), eventService.getEvent(1))
    }

    @Test
    fun `Test event get not found`() {
        every { eventRepository.findById(any()) } returns empty()
        val error = Assertions.assertThrows(
                ResourceNotFoundException::class.java,
                { eventService.getEvent(1) }
        )
        Assertions.assertEquals("Event not found with ID: 1", error.message)
    }

    @Test
    fun `Test event entity get`() {
        val event = Event(
                name = "testevent",
                password = "testeventpassword",
                numberOfTables = 1
        )
        every { eventRepository.findById(1) } returns Optional.of(event)
        Assertions.assertEquals(
                event, eventService.getEventEntity(1))
    }

    @Test
    fun `Test event entity get not found`() {
        val eventId: Long = 1
        every { eventRepository.findById(any()) } returns empty()
        val error = Assertions.assertThrows(
                ResourceNotFoundException::class.java,
                { eventService.getEventEntity(eventId) }
        )
        Assertions.assertEquals("Event not found with ID: ${eventId}", error.message)
    }

    @Test
    fun `Test event update should succeed`() {
        val eventCreateUpdate = EventCreateUpdateDTO(
                id = 1,
                name = "testupdateevent",
                password = "testupdateeventpassword",
                numberOfTables = 13
        )
        val event = Event(
                id = 1,
                name = "basicname",
                password = "basic password",
                numberOfTables = 5
        )
        val eventDTO = EventDTO(
                id = 1,
                name = "testupdateevent",
                password = "testupdateeventpassword",
                numberOfTables = 13
        )
        every { eventRepository.findById(any()) } returns Optional.of(event)
        every { eventRepository.save(any()) } returns event
        Assertions.assertEquals(
                eventDTO,
                eventService.createUpdateEvent(eventCreateUpdate)
        )
    }

    @Test
    fun `Test event update should fail when providing invalid id`() {
        val eventCreateUpdate = EventCreateUpdateDTO(
                id = 1,
                name = "testupdateevent",
                password = "testupdateeventpassword",
                numberOfTables = 13
        )
        every { eventRepository.findById(any()) } returns empty()
        val error = Assertions.assertThrows(
                ResourceNotFoundException::class.java,
                { eventService.getEventEntity(eventCreateUpdate.id!!) }
        )
        Assertions.assertEquals("Event not found with ID: ${eventCreateUpdate.id}", error.message)
    }

    @Test
    fun `Test create event should succeed`() {
        val eventCreateUpdate = EventCreateUpdateDTO(
                id = null,
                name = "testupdateevent",
                password = "testupdateeventpassword",
                numberOfTables = 13
        )
        val event = Event(
                id = 1,
                name = "testupdateevent",
                password = "testupdateeventpassword",
                numberOfTables = 13
        )
        val eventDto = EventDTO(
                id = 1,
                name = "testupdateevent",
                password = "testupdateeventpassword",
                numberOfTables = 13
        )
        every { eventRepository.save(any()) } returns event
        Assertions.assertEquals(
                eventDto,
                eventService.createUpdateEvent(eventCreateUpdate)
        )
    }

    @Test
    fun `Test event deletion`() {
        every { eventRepository.findById(1) } returns Optional.of(Event(
                id = null,
                name = "testevent",
                password = "testeventpassword",
                numberOfTables = 3,
        ))
        Assertions.assertEquals(
                Unit, eventService.deleteEvent(1))
    }

    @Test
    fun `Test event deletion not found`() {
        every { eventRepository.findById(any()) } returns empty()
        val error = Assertions.assertThrows(
                ResourceNotFoundException::class.java,
                { eventService.getEvent(1) }
        )
        Assertions.assertEquals("Event not found with ID: 1", error.message)
    }



}