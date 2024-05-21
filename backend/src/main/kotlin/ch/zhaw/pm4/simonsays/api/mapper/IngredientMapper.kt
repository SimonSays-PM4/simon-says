package ch.zhaw.pm4.simonsays.api.mapper

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.entity.Ingredient
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface IngredientMapper {

    @Mappings(
            Mapping(target= "event.ingredients", ignore = true)
    )
    fun mapToIngredientDTO(ingredient: Ingredient): IngredientDTO

    @Mappings(
            Mapping(target = "id", ignore = true),
            Mapping(target = "name", source = "ingredientDTO.name"),
            Mapping(target= "event.ingredients", ignore = true),
            Mapping(target= "event.menus", ignore = true),
            Mapping(target= "event.menuItems", ignore = true),
            Mapping(target= "event.stations", ignore = true),
            Mapping(target= "event.order", ignore = true),
            Mapping(target= "event.orderIngredient", ignore = true),
            Mapping(target= "event.orderMenuItem", ignore = true),
            Mapping(target= "event.orderMenu", ignore = true),
            Mapping(target= "menuItems", ignore = true),
            Mapping(target= "stations", ignore = true),
            Mapping(target= "orderIngredients", ignore = true),
    )
    fun mapDTOtoIngredient(ingredientDTO: IngredientDTO, event: EventDTO): Ingredient

    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "name", source = "ingredient.name"),
        Mapping(target = "event.ingredients", ignore = true),
        Mapping(target= "event.menus", ignore = true),
        Mapping(target= "event.menuItems", ignore = true),
        Mapping(target= "event.stations", ignore = true),
        Mapping(target= "event.order", ignore = true),
        Mapping(target= "event.orderIngredient", ignore = true),
        Mapping(target= "event.orderMenuItem", ignore = true),
        Mapping(target= "event.orderMenu", ignore = true),
        Mapping(target= "menuItems", ignore = true),
        Mapping(target= "stations", ignore = true),
        Mapping(target= "orderIngredients", ignore = true),
    )
    fun mapCreateDTOToIngredient(ingredient: IngredientCreateUpdateDTO, event: EventDTO): Ingredient
}