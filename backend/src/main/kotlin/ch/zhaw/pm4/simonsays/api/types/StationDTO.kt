package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema

data class StationDTO (
        @field:Schema(description = "ID of the ingredient")
        val id: Long,
        @field:Schema(description = "Name for the ingredient")
        val name: String,
        @field:Schema(description = "List of ingredients")
        var ingredients: List<IngredientDTO>
)