package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.mapper.EventMapperImpl
import ch.zhaw.pm4.simonsays.api.types.EventCreateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.service.EventService
import ch.zhaw.pm4.simonsays.service.EventServiceImpl
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EventTest {
    @MockkBean(relaxed = true)
    protected lateinit var eventRepo: EventRepository

    private lateinit var eventService: EventService

    @BeforeEach
    fun setup() {
        // mockkStatic("kotlinx.coroutines.reactor.MonoKt")
        eventRepo = mockk(relaxed = true);
        eventService = EventServiceImpl(eventRepo, EventMapperImpl())
    }

    @Test
    fun `Test event creation`() {
        every { eventRepo.save(any()) } returns Event(
            "Testevent",
                "Testeventpassword",
                15,
                1
        )
        val eventCreateDTO = EventCreateDTO(
                "Testevent",
                "Testeventpassword",
                15
        )
        Assertions.assertEquals(EventDTO(
                "Testevent",
                15,
                1
        ), eventService.createEvent(eventCreateDTO))
    }

    @Test
    fun `Test event fetching`() {
        every { eventRepo.findAll() } returns mutableListOf(
                Event(
                      "testevent",
                        "password",
                        3
                ),
                Event(
                    "testevent2",
                        "password",
                        2
                )
        )
        val events: List<EventDTO> = eventService.getEvents()
        Assertions.assertEquals(2, events.count())
    }

}