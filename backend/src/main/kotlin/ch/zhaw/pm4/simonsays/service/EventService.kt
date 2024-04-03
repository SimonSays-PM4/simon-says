package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.EventCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO

interface EventService {
    fun getEvents(): List<EventDTO>
    fun getEvent(eventId: Long): EventDTO
    fun createUpdateEvent(event: EventCreateUpdateDTO): EventDTO
    fun deleteEvent(eventId: Long): EventDTO
}