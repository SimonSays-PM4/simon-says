package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.entity.Ingredient

fun getEvent(id: Long = 1, ingredients: Set<Ingredient>? = null) = Event(id,"TestEvent", "TestPassword", 1, ingredients= ingredients)
fun getEventDTO() = EventDTO(id = 1, name = "TestEvent", password = "TestPassword", numberOfTables =  1)
