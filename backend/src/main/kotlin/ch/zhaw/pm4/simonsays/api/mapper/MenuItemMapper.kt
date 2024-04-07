package ch.zhaw.pm4.simonsays.api.mapper

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.MenuItem
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface MenuItemMapper {
    @Mapping(target = "eventId", source = "event.id")
    fun mapToMenuItemDTO(menuItem: MenuItem): MenuItemDTO

    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "name", source = "menuItem.name"),
        Mapping(target= "event.menuItems", ignore = true)
    )
    fun mapCreateDTOToMenuItem(menuItem: MenuItemCreateUpdateDTO, event: EventDTO): MenuItem
}