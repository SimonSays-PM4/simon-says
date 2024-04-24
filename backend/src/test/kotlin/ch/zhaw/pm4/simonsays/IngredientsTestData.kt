package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.entity.Ingredient

fun createUpdateTestIngredientDTO(id: Long? = null, name: String? = "TestIngredient") = IngredientCreateUpdateDTO(
    id = id,
    name = name
)

fun getTestIngredient1(name: String? = "TestIngredient") = Ingredient(name = name!!, id = 1, event = getTestEvent(), menuItems = null, stations = null)

fun getTestIngredientDTO(name: String? = "TestIngredient", id: Long? = 1) = IngredientDTO(name = name!!, id = id!!)

fun getTestEvent() = Event(id = 1, name = "TestEvent", password = "TestPassword", numberOfTables = 1)
fun getTestEventDTO() = EventDTO(id = 1, name = "TestEvent", password = "TestPassword", numberOfTables = 1)
