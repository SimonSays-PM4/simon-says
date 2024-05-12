package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.entity.OrderIngredient
import ch.zhaw.pm4.simonsays.entity.State
import ch.zhaw.pm4.simonsays.repository.OrderIngredientRepository
import org.springframework.stereotype.Service

@Service
class OrderIngredientService(
        private val orderIngredientRepository: OrderIngredientRepository,
) {
    fun getOrderIngredientByIngredientIds(ingredientIds: List<Long>): List<OrderIngredient> {
        return orderIngredientRepository.findAllByIngredientIdInAndStateEquals(ingredientIds, State.IN_PROGRESS)
    }

}