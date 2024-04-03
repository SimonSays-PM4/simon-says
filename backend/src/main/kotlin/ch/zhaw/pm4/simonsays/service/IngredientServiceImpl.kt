package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.IngredientMapper
import ch.zhaw.pm4.simonsays.api.types.IngredientCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import org.springframework.stereotype.Service

@Service
class IngredientServiceImpl(
    private val ingredientRepository: IngredientRepository,
    private val ingredientMapper: IngredientMapper
) : IngredientService {

    override fun listIngredients(): List<IngredientDTO> {
        return ingredientRepository.findAll().map { ingredientMapper.mapToIngredientDTO(it) }
    }

    override fun getIngredient(id: Long): IngredientDTO {
        return ingredientMapper.mapToIngredientDTO(ingredientRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Ingredient not found with ID: $id")
        })
    }

    override fun deleteIngredient(id: Long) {
        ingredientRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Ingredient not found with ID: $id")
        }
        ingredientRepository.deleteById(id)
    }

    override fun createUpdateIngredient(ingredient: IngredientCreateUpdateDTO): IngredientDTO {
        val ingredientToSave = if (ingredient.id != null) {
            makeIngredientReadyForUpdate(ingredient)
        } else {
            ingredientMapper.mapCreateDTOToIngredient(ingredient)
        }

        return ingredientMapper.mapToIngredientDTO(ingredientRepository.save(ingredientToSave))
    }

    private fun makeIngredientReadyForUpdate(ingredient: IngredientCreateUpdateDTO): Ingredient {
        val ingredientToSave = ingredientRepository.findById(ingredient.id!!).orElseThrow {
            ResourceNotFoundException("Ingredient not found with ID: ${ingredient.id}")
        }
        ingredientToSave.name = ingredient.name!!
        return ingredientToSave
    }


}