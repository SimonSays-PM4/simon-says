package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.StationCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.StationDTO

interface StationService {
    fun listStations(eventId: Long): List<StationDTO>
    fun getStation(stationId: Long, eventId: Long): StationDTO
    fun createUpdateStation(station: StationCreateUpdateDTO, eventId: Long): StationDTO
    fun deleteStation(stationId: Long, eventId: Long)
}