package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.entity.Event

fun getEvent(id: Long = 1) = Event(id,"TestEvent", "TestPassword", 1)
fun getEventDTO() = EventDTO(id = 1, name = "TestEvent", password = "TestPassword", numberOfTables =  1)
