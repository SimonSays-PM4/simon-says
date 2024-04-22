package ch.zhaw.pm4.simonsays.api.types

import ch.zhaw.pm4.simonsays.entity.OrderMenu
import ch.zhaw.pm4.simonsays.entity.OrderMenuItem
import ch.zhaw.pm4.simonsays.entity.State
import io.swagger.v3.oas.annotations.media.Schema

data class OrderDTO(
    @field:Schema(description = "Id of order")
    val id: Long,
    @field:Schema(description = "List of menus")
    val menus: List<OrderMenuDTO>?,
    @field:Schema(description = "List of menu items")
    var menuItems: List<OrderMenuItemDTO>?,
    @field:Schema(description = "Table number")
    var tableNumber: Long,
    @field:Schema(description = "the price of the order")
    var totalPrice: Long,
    @field:Schema(description = "the state of the order", enumAsRef = true)
    var state: State,
)