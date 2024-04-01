package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.EventMapper
import ch.zhaw.pm4.simonsays.api.types.EventCreateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.EventPutDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.EventRepository
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

    override fun getEvent(eventId: Long): EventDTO {
        val event = eventRepository.findById(eventId)
                .orElseThrow { ResourceNotFoundException("Event not found with ID: $eventId") }
        return eventMapper.mapToEventDTO(event)
    }

    override fun putEvent(event: EventPutDTO): EventDTO {
        val existingEvent = eventRepository.findById(event.id)

        val eventToSave = if (existingEvent.isPresent) {
            existingEvent.get().apply {
                this.name = event.name.toString()
                this.numberOfTables = event.numberOfTables!!
                this.password = event.password.toString()
            }

        } else {
            throw ResourceNotFoundException("Event not found with ID: ${event.id}")
        }

        val savedEvent = eventRepository.save(eventToSave)
        return eventMapper.mapToEventDTO(savedEvent)
    }

    override fun deleteEvent(eventId: Long): EventDTO {
        val event = eventRepository.findById(eventId).orElseThrow {
            ResourceNotFoundException("Event not found with ID: $eventId")
        }
        eventRepository.delete(event)
        return eventMapper.mapToEventDTO(event)
    }

}