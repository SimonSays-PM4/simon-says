package ch.zhaw.pm4.simonsays.api.mapper

import ch.zhaw.pm4.simonsays.api.types.*
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.Station
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface StationMapper {
    @Mappings(
            Mapping(target = "name", source = "station.name"),
    )
    fun mapToStationDTO(station: Station): StationDTO

    @Mappings(
            Mapping(target = "id", ignore = true),
            Mapping(target = "name", source = "station.name"),
            Mapping(target = "ingredients", source = "ingredients"),
            Mapping(target= "event.ingredients", ignore = true),
            Mapping(target= "event.menuItems", ignore = true),
            Mapping(target= "event.stations", ignore = true)
    )
    fun mapCreateDTOToStation(station: StationCreateUpdateDTO, event: EventDTO, ingredients: List<Ingredient>): Station
}