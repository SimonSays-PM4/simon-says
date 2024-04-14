package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema

data class MenuDTO (
    @field:Schema(description = "ID of the menu item")
    val id: Long,
    @field:Schema(description = "Name for the menu")
    val name: String,
    @field:Schema(description = "List of menu items")
    var menuItems: List<MenuItemDTO>,
    @field:Schema(description = "Price of the menu")
    var price: Long
)