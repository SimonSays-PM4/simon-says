package ch.zhaw.pm4.simonsays.service


import ch.zhaw.pm4.simonsays.api.types.IngredientCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO

interface IngredientService {

    fun listIngredients() : List<IngredientDTO>

    fun getIngredient(id: Long) : IngredientDTO

    fun deleteIngredient(id: Long)

    fun createUpdateIngredient(ingredient: IngredientCreateUpdateDTO) : IngredientDTO
}