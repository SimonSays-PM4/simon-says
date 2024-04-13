package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

class StationCreateUpdateDTO(
        @field:Schema(description = "ID for the station")
        val id: Long?,

        @field:Schema(description = "Name for the station")
        @field:NotBlank(message = "Station name must be provided")
        @field:NotEmpty(message = "Station name must be provided")
        @field:Length(min = 1, max = 64, message = "Station name must be between 1 and 64 chars long")
        val name: String?,

        @NotNull(message = "Ingredient list must be provided")
        @Size(min = 1, message = "A station must be mapped to at least one ingredient")
        val ingredients: List<IngredientDTO>?
) {}