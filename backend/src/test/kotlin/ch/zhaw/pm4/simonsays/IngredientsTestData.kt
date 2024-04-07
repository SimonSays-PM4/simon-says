package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.entity.Ingredient

fun createUpdateIngredientDTO(id: Long? = null, name: String? = "TestIngredient") = IngredientCreateUpdateDTO(
    id = id,
    name = name,
    eventId = 1
)

fun getIngredient1(name: String? = "TestIngredient") = Ingredient(name = name!!, id = 1, getEvent())

fun getIngredient1DTO(name: String? = "TestIngredient") = IngredientDTO(name = name!!, id = 1)

fun getEvent() = Event("TestEvent", "TestPassword", 1, 1)
fun getEventDTO() = EventDTO("TestEvent", "TestPassword", 1, 1)
