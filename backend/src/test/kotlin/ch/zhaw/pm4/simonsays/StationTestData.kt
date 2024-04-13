package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.api.types.StationCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.StationDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.Station

fun getCreateUpdateStationDTO(id: Long? = null, name: String? = "Station test", ingredients: List<IngredientDTO>? = listOf(getTestIngredientDTO())): StationCreateUpdateDTO {
    return StationCreateUpdateDTO(
            id,
            name,
            ingredients
    )
}

fun getStation(id: Long = 1, name: String = "Station test", event: Event = getEvent(), ingredients: List<Ingredient> = listOf(getTestIngredient1())): Station {
    return Station(
            id,
            name,
            event,
            ingredients
    )
}

fun getStationDTO(id: Long = 1, name: String = "Station test", ingredientDTOs: List<IngredientDTO> = listOf(getTestIngredientDTO())): StationDTO {
    return StationDTO(
            id,
            name,
            ingredientDTOs
    )
}