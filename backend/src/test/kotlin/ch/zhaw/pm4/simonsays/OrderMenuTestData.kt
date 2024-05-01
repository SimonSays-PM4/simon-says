package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.OrderMenuDTO
import ch.zhaw.pm4.simonsays.api.types.OrderMenuItemDTO
import ch.zhaw.pm4.simonsays.entity.*

fun getOrderMenu(
        id: Long = 1,
        name: String = "Test order menu",
        event: Event = getEvent(),
        menu: Menu = getMenu(),
        orderMenuItems: MutableList<OrderMenuItem> = mutableListOf(
                getOrderMenuItem(order = getOrder(), orderMenu = null)
        ),
        price: Double = 15.20,
        order: FoodOrder = getOrder(),
        state: State = State.IN_PROGRESS
): OrderMenu {
    return OrderMenu(
            id = id,
            name = name,
            event = event,
            menu = menu,
            orderMenuItems = orderMenuItems,
            order = order,
            state = state,
            price = price
    )
}

fun getOrderMenuDTO(
        id: Long = 1,
        name: String = "Test order menu",
        state: State = State.IN_PROGRESS,
        price: Double = 15.20,
        menuItems: List<OrderMenuItemDTO> = listOf(getOrderMenuItemDTO())
): OrderMenuDTO {
        return OrderMenuDTO(
                id = id,
                name = name,
                state = state,
                price = price,
                menuItems = menuItems
        )
}