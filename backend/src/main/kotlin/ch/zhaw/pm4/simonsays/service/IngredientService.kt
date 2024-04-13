package ch.zhaw.pm4.simonsays.service


import ch.zhaw.pm4.simonsays.api.types.IngredientCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO

interface IngredientService {
    fun listIngredients(eventId: Long) : List<IngredientDTO>
    fun getIngredient(id: Long, eventId: Long) : IngredientDTO
    fun deleteIngredient(id: Long, eventId: Long)
    fun createUpdateIngredient(ingredient: IngredientCreateUpdateDTO, eventId:Long) : IngredientDTO
}