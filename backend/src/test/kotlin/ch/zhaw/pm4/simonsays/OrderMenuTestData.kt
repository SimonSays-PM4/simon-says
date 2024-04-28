package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.entity.*

fun getOrderMenu(
        id: Long = 1,
        name: String = "Test order menu item",
        event: Event = getEvent(),
        menu: Menu = getMenu(),
        orderMenuItems: MutableSet<OrderMenuItem> = mutableSetOf(
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