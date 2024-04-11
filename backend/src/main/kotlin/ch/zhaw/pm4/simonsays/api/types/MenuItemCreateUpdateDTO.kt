package ch.zhaw.pm4.simonsays.api.types

import ch.zhaw.pm4.simonsays.entity.Ingredient
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import org.hibernate.validator.constraints.Length

class MenuItemCreateUpdateDTO(
    @field:Schema(description = "ID for the event")
    val id: Long?,

    @field:Schema(description = "Name for the Menu Item")
    @field:NotBlank(message = "Menu item name must be provided")
    @field:NotEmpty(message = "Menu item name must be provided")
    @field:Length(min = 1, max = 64, message = "Menu item name must be between 1 and 64 chars long")
    val name: String?,

    @NotNull(message = "Ingredient list must be provided")
    @Size(min = 1, message = "A menu item must have at least one ingredient")
    val ingredients: List<IngredientDTO>?
) {}