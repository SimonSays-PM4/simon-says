package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.IngredientMapper
import ch.zhaw.pm4.simonsays.api.types.IngredientCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import org.springframework.stereotype.Service

@Service
class IngredientService(
    private val ingredientRepository: IngredientRepository,
    private val ingredientMapper: IngredientMapper,
    private val eventService: EventService
) {

    fun listIngredients(eventId: Long): List<IngredientDTO> {
        return ingredientRepository.findAllByEventId(eventId).map { ingredientMapper.mapToIngredientDTO(it) }
    }

    fun getIngredient(id: Long, eventId: Long): IngredientDTO {
        return ingredientMapper.mapToIngredientDTO(ingredientRepository.findByIdAndEventId(id, eventId).orElseThrow {
            ResourceNotFoundException("Ingredient not found with ID: $id")
        })
    }

    fun deleteIngredient(id: Long, eventId: Long) {
        ingredientRepository.findByIdAndEventId(id, eventId).orElseThrow {
            ResourceNotFoundException("Ingredient not found with ID: $id")
        }
        ingredientRepository.deleteById(id)
    }

    fun createUpdateIngredient(ingredient: IngredientCreateUpdateDTO, eventId: Long): IngredientDTO {
        val event = eventService.getEvent(eventId)
        val ingredientToSave = if (ingredient.id != null) {
            makeIngredientReadyForUpdate(ingredient, eventId)
        } else {
            ingredientMapper.mapCreateDTOToIngredient(ingredient, event)
        }

        return ingredientMapper.mapToIngredientDTO(ingredientRepository.save(ingredientToSave))
    }

    private fun makeIngredientReadyForUpdate(ingredient: IngredientCreateUpdateDTO, eventId: Long): Ingredient {
        val ingredientToSave = ingredientRepository.findByIdAndEventId(ingredient.id!!, eventId).orElseThrow {
            ResourceNotFoundException("Ingredient not found with ID: ${ingredient.id}")
        }
        ingredientToSave.name = ingredient.name!!
        ingredientToSave.mustBeProduced = ingredient.mustBeProduced!!
        return ingredientToSave
    }


}