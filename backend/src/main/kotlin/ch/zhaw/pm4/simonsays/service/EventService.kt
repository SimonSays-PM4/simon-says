package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.EventMapper
import ch.zhaw.pm4.simonsays.api.types.EventCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.exception.ResourceInUseException
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.EventRepository
import org.springframework.stereotype.Service

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val eventMapper: EventMapper
) {

    fun getEvents(): MutableList<EventDTO> {
        val events: MutableList<Event> = eventRepository.findAll()
        val eventDTOs: MutableList<EventDTO> = events.map { event ->
            eventMapper.mapToEventDTO(event)
        }.toMutableList()
        return eventDTOs
    }

    fun getEvent(eventId: Long): EventDTO {
        val event = eventRepository.findById(eventId)
            .orElseThrow { ResourceNotFoundException("Event not found with ID: $eventId") }
        return eventMapper.mapToEventDTO(event)
    }

    fun getEventEntity(eventId: Long): Event {
        val event = eventRepository.findById(eventId)
            .orElseThrow { ResourceNotFoundException("Event not found with ID: $eventId") }
        return event
    }

    fun createUpdateEvent(event: EventCreateUpdateDTO): EventDTO {

        val eventToBeSaved = if (event.id != null) {
            makeEventReadyForUpdate(event)
        } else {
            eventMapper.mapCreateDTOToEvent(event)
        }

        val savedEvent = eventRepository.save(eventToBeSaved)
        return eventMapper.mapToEventDTO(savedEvent)
    }

    fun deleteEvent(eventId: Long) {
        val event = getEventEntity(eventId)
        if(
                !event.menuItems.isNullOrEmpty() ||
                !event.stations.isNullOrEmpty() ||
                !event.ingredients.isNullOrEmpty() ||
                !event.menus.isNullOrEmpty()
        ) {
            throw ResourceInUseException("Event is used and cannot be deleted")
        }
        eventRepository.delete(event)
    }

    private fun makeEventReadyForUpdate(event: EventCreateUpdateDTO): Event {
        val eventToSave = getEventEntity(event.id!!)
        eventToSave.name = event.name!!
        eventToSave.password = event.password!!
        eventToSave.numberOfTables = event.numberOfTables!!
        return eventToSave
    }

}