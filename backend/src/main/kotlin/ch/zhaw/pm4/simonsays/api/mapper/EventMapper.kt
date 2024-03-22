package ch.zhaw.pm4.simonsays.api.mapper

import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.entity.Event
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface EventMapper {
    fun mapToEventDTO(event: Event): EventDTO
}