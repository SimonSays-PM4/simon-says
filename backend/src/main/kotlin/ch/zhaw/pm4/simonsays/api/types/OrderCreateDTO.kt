package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class OrderCreateDTO(
    @field:Schema(description = "List of menus")
    val menus: List<MenuDTO>?,
    @field:Schema(description = "List of menu items")
    var menuItems: List<MenuItemDTO>?,
    @field:Schema(description = "Table number")
    var tableNumber: Long?,
    @field:Schema(description = "indicates if the order is a take away order")
    @field:NotNull(message = "IsTakeAway must be provided")
    var isTakeAway: Boolean?
)