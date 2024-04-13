package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.StationMapper
import ch.zhaw.pm4.simonsays.api.types.StationCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.StationDTO
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.Station
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import ch.zhaw.pm4.simonsays.repository.StationRepository
import org.springframework.stereotype.Service

@Service
class StationServiceImpl(
        private val stationRepository: StationRepository,
        private val stationMapper: StationMapper,
        private val eventService: EventService,
        private val ingredientRepository: IngredientRepository,
) : StationService {

    override fun listStations(eventId: Long): MutableList<StationDTO> {
        val stations: List<Station> = stationRepository.findAllByEventId(eventId)
        val stationDTOs: MutableList<StationDTO> = stations.map { station ->
            stationMapper.mapToStationDTO(station)
        }.toMutableList()
        return stationDTOs
    }

    override fun getStation(stationId: Long, eventId: Long): StationDTO {
        val station = stationRepository.findByIdAndEventId(stationId, eventId)
                .orElseThrow { ResourceNotFoundException("Station not found with ID: $stationId") }
        return stationMapper.mapToStationDTO(station)
    }

    override fun createUpdateStation(station: StationCreateUpdateDTO, eventId: Long): StationDTO {
        val event = eventService.getEvent(eventId)
        val ingredients = ingredientRepository.findByIdIn(station.ingredients!!.map { it.id.toInt() })
        val isUpdateOperation = station.id != null
        val stationToBeSaved = if (isUpdateOperation) {
            makeStationReadyForUpdate(station, eventId, ingredients)
        } else {
            stationMapper.mapCreateDTOToStation(station, event, ingredients)
        }
        val savedStation = stationRepository.save(stationToBeSaved)
        return stationMapper.mapToStationDTO(savedStation)
    }


    private fun makeStationReadyForUpdate(station: StationCreateUpdateDTO, eventId: Long, ingredients: List<Ingredient>): Station {
        val stationToSave = stationRepository.findById(station.id!!).orElseThrow {
            ResourceNotFoundException("Station not found with ID: ${station.id}")
        }
        stationToSave.name = station.name!!
        stationToSave.event = eventService.getEventEntity(eventId)
        stationToSave.ingredients = ingredients
        return stationToSave
    }

    override fun deleteStation(stationId: Long, eventId: Long) {
        val menuItem = stationRepository.findByIdAndEventId(stationId, eventId).orElseThrow {
            ResourceNotFoundException("Station not found with ID: $stationId")
        }
        stationRepository.delete(menuItem)
    }

}