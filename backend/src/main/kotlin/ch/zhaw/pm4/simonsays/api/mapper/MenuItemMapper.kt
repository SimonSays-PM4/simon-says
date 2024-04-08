package ch.zhaw.pm4.simonsays.api.mapper

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.MenuItem
import org.apache.commons.lang3.mutable.Mutable
import org.mapstruct.*

@Mapper(componentModel = "spring")
interface MenuItemMapper {
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "ingredientIds", ignore = true) // Handle manually in after mapping
    fun mapToMenuItemDTO(menuItem: MenuItem): MenuItemDTO

    @Mappings(
            Mapping(target = "id", ignore = true),
            Mapping(target = "name", source = "menuItem.name"),
            Mapping(target = "event", ignore = true), // Event is set in the service layer
            Mapping(target = "ingredients", source = "ingredients") // Handled separately
    )
    fun mapCreateDTOToMenuItem(menuItem: MenuItemCreateUpdateDTO, event: EventDTO, ingredients: MutableList<IngredientDTO>): MenuItem
}
