package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.EventCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.entity.Event

interface EventService {
    fun getEvents(): List<EventDTO>
    fun getEvent(eventId: Long): EventDTO
    fun getEventEntity(eventId: Long): Event
    fun createUpdateEvent(event: EventCreateUpdateDTO): EventDTO
    fun deleteEvent(eventId: Long)

}