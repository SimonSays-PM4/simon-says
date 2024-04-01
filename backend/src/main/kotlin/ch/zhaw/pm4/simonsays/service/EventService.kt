package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.EventCreateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.EventPutDTO

interface EventService {
    fun createEvent(event: EventCreateDTO): EventDTO
    fun getEvents(): List<EventDTO>
    fun getEvent(eventId: Long): EventDTO
    fun putEvent(event: EventPutDTO): EventDTO
    fun deleteEvent(eventId: Long): EventDTO
}