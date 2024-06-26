package ch.zhaw.pm4.simonsays.api.mapper

import ch.zhaw.pm4.simonsays.api.types.EventCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.entity.Event
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface EventMapper {
    fun mapToEventDTO(event: Event): EventDTO

    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target= "ingredients", ignore = true),
        Mapping(target= "menuItems", ignore = true),
        Mapping(target= "stations", ignore = true),
        Mapping(target= "menus", ignore = true),
        Mapping(target= "order", ignore = true),
        Mapping(target= "orderIngredient", ignore = true),
        Mapping(target= "orderMenuItem", ignore = true),
        Mapping(target= "orderMenu", ignore = true)
    )
    fun mapCreateDTOToEvent(event: EventCreateUpdateDTO): Event
}