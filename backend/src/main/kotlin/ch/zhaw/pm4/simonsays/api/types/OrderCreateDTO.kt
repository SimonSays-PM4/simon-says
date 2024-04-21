package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema

data class OrderCreateDTO(
    @field:Schema(description = "List of menus")
    val menus: List<MenuDTO>,
    @field:Schema(description = "List of menu items")
    var menuItems: List<MenuItemDTO>,
    @field:Schema(description = "Table number")
    var tableNumber: Long,
)