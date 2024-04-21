package ch.zhaw.pm4.simonsays.api.types

import ch.zhaw.pm4.simonsays.entity.State
import io.swagger.v3.oas.annotations.media.Schema

class OrderMenuItemDTO(
    @field:Schema(description = "ID of the menu item")
    val id: Long,
    @field:Schema(description = "Name for the menu item")
    val name: String,
    @field:Schema(description = "List of ingredients")
    var ingredients: List<OrderIngredientDTO>,
    @field:Schema(description = "Price of the menu item")
    var price: Double,
    @field:Schema(description = "the state of the order", enumAsRef = true)
    var state: State,
)