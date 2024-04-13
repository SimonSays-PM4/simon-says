package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.IngredientCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.entity.Ingredient

fun createUpdateTestIngredientDTO(id: Long? = null, name: String? = "TestIngredient") = IngredientCreateUpdateDTO(
    id = id,
    name = name
)

fun getTestIngredient1(name: String? = "TestIngredient") = Ingredient(name = name!!, id = 1, event = getTestEvent())

fun getTestIngredientDTO(name: String? = "TestIngredient", id: Long? = 1) = IngredientDTO(name = name!!, id = id!!)

fun getTestEvent() = Event("TestEvent", "TestPassword", 1, 1)
fun getTestEventDTO() = EventDTO("TestEvent", "TestPassword", 1, 1)
