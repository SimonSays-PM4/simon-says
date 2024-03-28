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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
/*
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class EventTest {

    @MockkBean(relaxed = true)
    protected lateinit var eventRepo: EventRepository

    private lateinit var eventService: EventService

    @BeforeEach
    fun setup() {
        // mockkStatic("kotlinx.coroutines.reactor.MonoKt")
        eventService = EventServiceImpl(eventRepo, EventMapperImpl())
    }

    @Test
    fun `Test event creation`() {
        every { eventRepo.save(any()) } returns Event(
            "Testevent",
                "Testeventpassword",
                15
        )
        val eventCreateDTO = EventCreateDTO(
                "Testevent",
                "Testeventpassword",
                15
        )
        Assertions.assertEquals(EventDTO(
                "Testevent",
                15
        ), eventService.createEvent(eventCreateDTO))
    }

}*/