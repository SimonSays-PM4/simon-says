package ch.zhaw.pm4.simonsays.api.mapper

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.MenuCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuDTO
import ch.zhaw.pm4.simonsays.entity.Menu
import ch.zhaw.pm4.simonsays.entity.MenuItem
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface MenuMapper {
    @Mappings(
        Mapping(target = "name", source = "menu.name"),
        Mapping(target = "menuItems")
    )
    fun mapToMenuDTO(menu: Menu, price: Double): MenuDTO

    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "name", source = "menu.name"),
        Mapping(target = "menuItems", source = "menuItems"),
        Mapping(target= "event.menus", ignore = true),
        Mapping(target= "event.menuItems", ignore = true),
        Mapping(target= "event.ingredients", ignore = true)
    )
    fun mapCreateDTOToMenu(menu: MenuCreateUpdateDTO, event: EventDTO, menuItems: List<MenuItem>): Menu
}