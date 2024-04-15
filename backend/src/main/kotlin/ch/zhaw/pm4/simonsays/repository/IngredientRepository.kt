package ch.zhaw.pm4.simonsays.repository

import ch.zhaw.pm4.simonsays.entity.Ingredient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface IngredientRepository : JpaRepository<Ingredient, Long> {
    fun findAllByEventId(eventId: Long): List<Ingredient>
    fun findByIdAndEventId(id: Long, eventId: Long): Optional<Ingredient>
    fun findByIdIn(id: List<Long>): List<Ingredient>
}