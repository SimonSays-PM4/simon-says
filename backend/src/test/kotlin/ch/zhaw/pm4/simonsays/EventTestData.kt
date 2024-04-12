package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.entity.Event

fun getEvent() = Event("TestEvent", "TestPassword", 1, 1)
fun getEventDTO() = EventDTO("TestEvent", "TestPassword", 1, 1)
