package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.api.types.StationCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.StationDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.Station

fun getCreateUpdateStationDTO(id: Long? = null, name: String? = "Station test", assemblyStation: Boolean = false, ingredients: List<IngredientDTO>? = listOf(getTestIngredientDTO())): StationCreateUpdateDTO {
    return StationCreateUpdateDTO(
            id,
            name,
            assemblyStation,
            ingredients
    )
}

fun getStation(id: Long = 1, name: String = "Station test", assemblyStation: Boolean = false, event: Event = getEvent(), ingredients: List<Ingredient> = listOf(getTestIngredient1())): Station {
    return Station(
            id,
            name,
            assemblyStation,
            event,
            ingredients
    )
}

fun getStationDTO(id: Long = 1, name: String = "Station test", assemblyStation: Boolean = false, ingredientDTOs: List<IngredientDTO> = listOf(getTestIngredientDTO())): StationDTO {
    return StationDTO(
            id,
            name,
            assemblyStation,
            ingredientDTOs
    )
}