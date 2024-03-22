package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import org.springframework.stereotype.Service

@Service
class EventServiceImpl: EventService {
    override fun createEvent(): EventDTO {
        return EventDTO(
                name = "Testevent",
                password = "Test12345!",
                numberOfTables = 12
        );
    }
}