package ch.zhaw.pm4.simonsays.repository

import ch.zhaw.pm4.simonsays.entity.OrderIngredient
import ch.zhaw.pm4.simonsays.entity.State
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderIngredientRepository: JpaRepository<OrderIngredient, Long> {
    fun findAllByIngredientIdInAndStateEquals(ingredientIds: List<Long>, state: State ): List<OrderIngredient>
}