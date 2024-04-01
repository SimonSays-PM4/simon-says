package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.IngredientCreateDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO

interface IngredientService {

    fun createIngredient(ingredient: IngredientCreateDTO) : IngredientDTO

    fun listIngredients() : List<IngredientDTO>

    fun getIngredient(id: Long) : IngredientDTO

    fun deleteIngredient(id: Long)
}