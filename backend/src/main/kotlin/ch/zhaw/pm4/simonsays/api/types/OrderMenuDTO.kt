package ch.zhaw.pm4.simonsays.api.types

import ch.zhaw.pm4.simonsays.entity.State
import io.swagger.v3.oas.annotations.media.Schema

class OrderMenuDTO(
    @field:Schema(description = "ID of the menu")
    val id: Long,
    @field:Schema(description = "Name for the menu")
    val name: String,
    @field:Schema(description = "List of menu items")
    var menuItems: List<OrderMenuItemDTO>,
    @field:Schema(description = "Price of the menu")
    var price: Long,
    @field:Schema(description = "the state of the order", enumAsRef = true)
    var state: State,
)