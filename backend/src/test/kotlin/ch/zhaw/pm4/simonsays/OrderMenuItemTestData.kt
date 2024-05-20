package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.OrderIngredientDTO
import ch.zhaw.pm4.simonsays.api.types.OrderMenuItemDTO
import ch.zhaw.pm4.simonsays.entity.*

fun getOrderMenuItem(
        id: Long = 1,
        name: String = "Test order menu item",
        event: Event = getEvent(),
        menuItem: MenuItem = getMenuItem(),
        orderIngredient: MutableList<OrderIngredient> = mutableListOf(
                getOrderIngredient()
        ),
        price: Double = 15.20,
        orderMenu: OrderMenu? = null,
        order: FoodOrder?,
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

fun getOrderMenuItemDTO(
        id: Long = 1,
        name: String = "Test order menu item",
        state: State = State.IN_PROGRESS,
        ingredients: List<OrderIngredientDTO> = listOf(getOrderIngredientDTO()),
        price: Double = 15.20
): OrderMenuItemDTO {
        return OrderMenuItemDTO(
                id = id,
                name = name,
                state = state,
                ingredients = ingredients,
                price = price
        )
}