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
        Mapping(target= "menuItems", ignore = true)
    )
    fun mapCreateDTOToEvent(event: EventCreateUpdateDTO): Event
}