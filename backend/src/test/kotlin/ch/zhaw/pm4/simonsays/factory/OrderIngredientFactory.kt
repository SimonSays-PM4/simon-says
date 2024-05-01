package ch.zhaw.pm4.simonsays.factory

import ch.zhaw.pm4.simonsays.entity.*
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import ch.zhaw.pm4.simonsays.repository.OrderIngredientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderIngredientFactory(
        @Autowired private val orderIngredientRepository: OrderIngredientRepository,
        @Autowired private val ingredientRepository: IngredientRepository,
        @Autowired private val eventRepository: EventRepository
) {
    fun createOrderIngredient(
        name: String = "Test Order ingredient",
        eventId: Long = 1,
        ingredientId: Long = 1,
        orderMenuItem: OrderMenuItem? = null,
        state: State = State.IN_PROGRESS
    ): OrderIngredient {
        val event = eventRepository.findById(eventId).orElse(null)
        val ingredient = ingredientRepository.findById(ingredientId).orElse(null)
        val orderIngredient = OrderIngredient(
                name = name,
                event = event,
                ingredient = ingredient,
                orderMenuItem = orderMenuItem,
                state = state
        )
        return orderIngredientRepository.save(orderIngredient)
    }
}