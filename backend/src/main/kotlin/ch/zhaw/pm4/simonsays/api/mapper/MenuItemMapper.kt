package ch.zhaw.pm4.simonsays.api.mapper

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.MenuItem
import org.apache.commons.lang3.mutable.Mutable
import org.mapstruct.*

@Mapper(componentModel = "spring")
interface MenuItemMapper {
    @Mappings(
        Mapping(target = "eventId", source = "event.id"),
        Mapping(target = "name", source = "menuItem.name"),
        Mapping(target = "ingredients")
    )
    fun mapToMenuItemDTO(menuItem: MenuItem): MenuItemDTO

    @Mappings(
            Mapping(target = "id", ignore = true),
            Mapping(target = "name", source = "menuItem.name"),
            Mapping(target = "ingredients", source = "ingredients"),
            Mapping(target= "event.menuItems", ignore = true),
            Mapping(target= "event.ingredients", ignore = true)
    )
    fun mapCreateDTOToMenuItem(menuItem: MenuItemCreateUpdateDTO, event: EventDTO, ingredients: List<Ingredient>): MenuItem
}
