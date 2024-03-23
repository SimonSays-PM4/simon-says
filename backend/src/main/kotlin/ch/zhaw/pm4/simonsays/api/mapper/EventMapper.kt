package ch.zhaw.pm4.simonsays.api.mapper

import ch.zhaw.pm4.simonsays.api.types.EventCreateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.entity.Event
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface EventMapper {
    fun mapToEventDTO(event: Event): EventDTO

    @Mapping(target = "id", ignore = true)
    fun mapCreateDTOToEvent(event: EventCreateDTO): Event
}