package ch.zhaw.pm4.simonsays.api.mapper

import ch.zhaw.pm4.simonsays.api.types.IngredientCreateDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.entity.Ingredient
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface IngredientMapper {

    fun mapToIngredientDTO(ingredient: Ingredient): IngredientDTO

    @Mapping(target = "id", ignore = true)
    fun mapCreateDTOToIngredient(ingredient: IngredientCreateDTO): Ingredient
}