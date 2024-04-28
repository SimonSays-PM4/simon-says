package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.entity.*

fun getOrder(
        id: Long = 1,
        event: Event = getEvent(),
        state: State = State.IN_PROGRESS,
        tableNumber: Long = 1,
        totalPrice: Double = 0.0
): FoodOrder {
    return FoodOrder(
        id = id,
        event = event,
        state = state,
        tableNumber = tableNumber,
        totalPrice = totalPrice
    )
}