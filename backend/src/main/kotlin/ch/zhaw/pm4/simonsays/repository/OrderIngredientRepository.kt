package ch.zhaw.pm4.simonsays.repository

import ch.zhaw.pm4.simonsays.entity.OrderIngredient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderIngredientRepository: JpaRepository<OrderIngredient, Long> {
    fun findAllByIngredientId(ingredientId: Long): List<OrderIngredient>
}