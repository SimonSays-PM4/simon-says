package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.entity.*

fun getOrderMenuItem(
        id: Long = 1,
        name: String = "Test order menu item",
        event: Event = getEvent(),
        menuItem: MenuItem = getMenuItem(),
        orderIngredient: MutableSet<OrderIngredient> = mutableSetOf(
                getOrderIngredient()
        ),
        price: Double = 15.20,
        orderMenu: OrderMenu?,
        order: FoodOrder,
        state: State = State.IN_PROGRESS
): OrderMenuItem {
    return OrderMenuItem(
            id = id,
            name = name,
            event = event,
            menuItem = menuItem,
            orderIngredients = orderIngredient,
            price = price,
            orderMenu = orderMenu,
            order = order,
            state = state
    )
}