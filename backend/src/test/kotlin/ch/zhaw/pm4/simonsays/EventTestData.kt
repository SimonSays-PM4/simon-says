package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.entity.Event

fun getEvent() = Event(1,"TestEvent", "TestPassword", 1)
fun getEventDTO() = EventDTO(id = 1, name = "TestEvent", password = "TestPassword", numberOfTables =  1)
