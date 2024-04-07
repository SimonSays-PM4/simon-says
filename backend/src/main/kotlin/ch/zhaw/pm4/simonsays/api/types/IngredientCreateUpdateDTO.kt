package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.Length


data class IngredientCreateUpdateDTO (

        @field:Schema(description = "Id for the ingredient", required = false)
        val id: Long?,

        @field:Schema(description = "Name for the ingredient")
        @field:NotBlank(message = "Ingredient name must be provided")
        @field:NotEmpty(message = "Ingredient name must be provided")
        @field:Length(min = 3, max = 64, message = "Ingredient name must be between 3 and 64 chars long")
        val name: String?,

        @field:Schema(description = "Event id for the ingredient")
        val eventId: Long
)