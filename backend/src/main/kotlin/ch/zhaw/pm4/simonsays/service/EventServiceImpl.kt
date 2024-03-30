package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.EventMapper
import ch.zhaw.pm4.simonsays.api.types.EventCreateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.exception.ValidationException
import ch.zhaw.pm4.simonsays.repository.EventRepository
import org.springframework.stereotype.Service

@Service
class EventServiceImpl(
        private val eventRepository: EventRepository,
        private val eventMapper: EventMapper
) : EventService {
    override fun createEvent(event: EventCreateDTO): EventDTO {
        // TODO exception handling
        if(event.numberOfTables < 0) {
            throw ValidationException("Number of Tables must be 0 or higher")
        }
        return eventMapper.mapToEventDTO( eventRepository.save(eventMapper.mapCreateDTOToEvent(event)))
    }
}