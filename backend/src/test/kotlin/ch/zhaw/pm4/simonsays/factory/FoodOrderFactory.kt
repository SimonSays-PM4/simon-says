package ch.zhaw.pm4.simonsays.factory

import ch.zhaw.pm4.simonsays.entity.*
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.repository.OrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FoodOrderFactory(
        @Autowired private val foodOrderRepository: OrderRepository,
        @Autowired private val eventRepository: EventRepository
) {
    fun createOrder(
        eventId: Long = 1,
        state: State = State.IN_PROGRESS,
        tableNumber: Long? = null,
        totalPrice: Double = 0.0,
        isTakeAway: Boolean = false,
    ): FoodOrder {
        val event = eventRepository.findById(eventId).orElse(null)
        val order = FoodOrder(
            event = event,
            state = state,
            tableNumber = tableNumber,
            totalPrice = totalPrice,
            isTakeAway = isTakeAway
        )
        return foodOrderRepository.save(order)
    }
}