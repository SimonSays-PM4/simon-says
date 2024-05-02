package ch.zhaw.pm4.simonsays.api.mapper

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.MenuItem
import org.mapstruct.*

@Mapper(componentModel = "spring")
interface MenuItemMapper {
    @Mappings(
        Mapping(target = "name", source = "menuItem.name"),
    )
    fun mapToMenuItemDTO(menuItem: MenuItem): MenuItemDTO

    @Mappings(
            Mapping(target = "id", ignore = true),
            Mapping(target = "name", source = "menuItem.name"),
            Mapping(target = "ingredients", source = "ingredients"),
            Mapping(target= "event.ingredients", ignore = true),
            Mapping(target= "event.menus", ignore = true),
            Mapping(target= "event.menuItems", ignore = true),
            Mapping(target= "event.stations", ignore = true),
            Mapping(target= "event.order", ignore = true),
            Mapping(target= "event.orderIngredient", ignore = true),
            Mapping(target= "event.orderMenuItem", ignore = true),
            Mapping(target= "event.orderMenu", ignore = true),
            Mapping(target= "menus", ignore = true),
            Mapping(target= "orderMenuItem", ignore = true),
    )
    fun mapCreateDTOToMenuItem(menuItem: MenuItemCreateUpdateDTO, event: EventDTO, ingredients: List<Ingredient>): MenuItem
}
