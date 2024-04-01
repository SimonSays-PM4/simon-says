package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank


data class IngredientCreateDTO (
        @field:Schema(description = "Name for the ingredient")
        @field:NotBlank(message = "Ingredient name must be provided")
        val name: String,
)