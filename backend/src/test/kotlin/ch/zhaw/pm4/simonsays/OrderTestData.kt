package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.*
import ch.zhaw.pm4.simonsays.entity.*

fun getOrder(
    id: Long = 1,
    event: Event = getEvent(),
    menus: MutableSet<OrderMenu>? = mutableSetOf(),
    menuItems: MutableSet<OrderMenuItem>? = mutableSetOf(),
    totalPrice: Double = 15.20,
    state: State = State.IN_PROGRESS,
    tableNumber: Long? = 1,
    isTakeAway: Boolean = false
): FoodOrder {
    return FoodOrder(
        id = id,
        event = event,
        menus = menus,
        menuItems = menuItems,
        totalPrice = totalPrice,
        state = state,
        tableNumber = tableNumber,
        isTakeAway = isTakeAway
    )
}

fun getOrderCreateDTO(
    menus: List<MenuDTO>? = listOf(),
    menuItems: List<MenuItemDTO>? = listOf(),
    tableNumber: Long? = 1,
    isTakeAway: Boolean = false
): OrderCreateDTO {
    return OrderCreateDTO(
        menus = menus,
        menuItems = menuItems,
        tableNumber = tableNumber,
        isTakeAway = isTakeAway
    )
}

fun getOrderDTO(
    id: Long = 1,
    menus: List<OrderMenuDTO>? = listOf(),
    menuItems: List<OrderMenuItemDTO>? = listOf(),
    totalPrice: Double = 15.20,
    state: State = State.IN_PROGRESS,
    tableNumber: Long? = 1,
    isTakeAway: Boolean = false
): OrderDTO {
    return OrderDTO(
        id = id,
        menus = menus,
        menuItems = menuItems,
        totalPrice = totalPrice,
        state = state,
        tableNumber = tableNumber,
        isTakeAway = isTakeAway
    )
}




