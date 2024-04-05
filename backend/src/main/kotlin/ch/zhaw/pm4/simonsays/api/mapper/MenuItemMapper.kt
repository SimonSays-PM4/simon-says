package ch.zhaw.pm4.simonsays.api.mapper

import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.MenuItem
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface MenuItemMapper {
    fun mapToMenuItemDTO(menuItem: MenuItem): MenuItemDTO

    @Mapping(target = "id", ignore = true)
    fun mapCreateDTOToMenuItem(menuItem: MenuItemCreateUpdateDTO): MenuItem
}