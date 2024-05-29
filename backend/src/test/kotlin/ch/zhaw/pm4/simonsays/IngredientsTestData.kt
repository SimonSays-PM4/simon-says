package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.MenuItem

fun createUpdateTestIngredientDTO(id: Long? = null, name: String? = "TestIngredient", mustBeProduced: Boolean = true) = IngredientCreateUpdateDTO(
    id = id,
    name = name,
    mustBeProduced = mustBeProduced
)

fun getTestIngredient1(name: String? = "TestIngredient", mustBeProduced: Boolean = true, menuItems: List<MenuItem>? = null) = Ingredient(name = name!!, id = 1, event = getTestEvent(), mustBeProduced = mustBeProduced, menuItems = menuItems, stations = null)

fun getTestIngredientDTO(name: String? = "TestIngredient", id: Long? = 1, mustBeProduced: Boolean = true) = IngredientDTO(name = name!!, id = id!!, mustBeProduced = mustBeProduced)

fun getTestEvent() = Event(id = 1, name = "TestEvent", password = "TestPassword", numberOfTables = 1)
fun getTestEventDTO() = EventDTO(id = 1, name = "TestEvent", password = "TestPassword", numberOfTables = 1)
