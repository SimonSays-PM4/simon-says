package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import org.hibernate.validator.constraints.Length

class MenuItemCreateUpdateDTO(
    @field:Schema(description = "ID for the menu item")
    val id: Long?,

    @field:Schema(description = "Name for the Menu Item")
    @field:NotBlank(message = "Menu item name must be provided")
    @field:NotEmpty(message = "Menu item name must be provided")
    @field:Length(min = 1, max = 64, message = "Menu item name must be between 1 and 64 chars long")
    val name: String?,

    @field:NotNull(message = "Ingredient list must be provided")
    @field:Size(min = 1, message = "A menu item must have at least one ingredient")
    val ingredients: List<IngredientDTO>?,

    @field:Schema(description = "Price of the menu item")
    @field:Min(value = 1, message = "Price of the menu item must be 1 or higher")
    val price: Double
) {}