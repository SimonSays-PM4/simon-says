package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.repository.EventRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class EventServiceImpl(
        private val eventRepository: EventRepository
) : EventService {
    override fun createEvent(): EventDTO {
        eventRepository.save(Event(
                name = "Testevent",
                password = "Test12345!",
                numberOfTables = 12
        ))
        return EventDTO(
                name = "Testeventt",
                password = "Test12345!",
                numberOfTables = 12
        )
    }
}