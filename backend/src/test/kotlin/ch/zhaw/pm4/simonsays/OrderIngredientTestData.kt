package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.OrderIngredientDTO
import ch.zhaw.pm4.simonsays.entity.*


fun getOrderIngredient(
        id: Long = 1,
        name: String = "Order Ingredient Test",
        event: Event = getEvent(),
        ingredient: Ingredient = getTestIngredient1(),
        orderMenuItem: OrderMenuItem? = null,
        state: State = State.IN_PROGRESS
): OrderIngredient {
    return OrderIngredient(
            id = id,
            name = name,
            event = event,
            ingredient = ingredient,
            orderMenuItem = orderMenuItem,
            state = state
    )
}
fun getOrderIngredientDTO(
        id: Long = 1,
        name: String = "Order Ingredient Test",
        state: State = State.IN_PROGRESS
): OrderIngredientDTO {
    return OrderIngredientDTO(
            id = id,
            name = name,
            state = state
    )
}