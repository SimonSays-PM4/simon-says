package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema

data class IngredientDTO (
    @field:Schema(description = "Id for the ingredient")
    val id: Long,
    @field:Schema(description = "Name for the ingredient")
    val name: String,
)