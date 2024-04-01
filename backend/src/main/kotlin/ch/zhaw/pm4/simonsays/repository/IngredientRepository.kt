package ch.zhaw.pm4.simonsays.repository

import ch.zhaw.pm4.simonsays.entity.Ingredient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IngredientRepository : JpaRepository<Ingredient, Long>