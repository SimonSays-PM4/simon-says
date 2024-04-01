package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.EventMapper
import ch.zhaw.pm4.simonsays.api.types.EventCreateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.exception.ValidationException
import ch.zhaw.pm4.simonsays.repository.EventRepository
import jakarta.validation.Valid
import org.springframework.stereotype.Service

@Service
class EventServiceImpl(
        private val eventRepository: EventRepository,
        private val eventMapper: EventMapper
) : EventService {
    override fun createEvent(event: EventCreateDTO): EventDTO {
        return eventMapper.mapToEventDTO( eventRepository.save(eventMapper.mapCreateDTOToEvent(event)))
    }

    override fun getEvents(): MutableList<EventDTO> {
        val events: MutableList<Event> = eventRepository.findAll()
        val eventDTOs: MutableList<EventDTO> = events.map { event ->
            eventMapper.mapToEventDTO(event)
        }.toMutableList()
        return eventDTOs
    }

}