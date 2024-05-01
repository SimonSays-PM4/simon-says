package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.*

interface StationService {
    fun listStations(eventId: Long): List<StationDTO>
    fun getStation(stationId: Long, eventId: Long): StationDTO
    fun createUpdateStation(station: StationCreateUpdateDTO, eventId: Long): StationDTO
    fun deleteStation(stationId: Long, eventId: Long)
    fun getStationView(stationId: Long, eventId: Long): List<OrderIngredientDTO>
    fun getAssemblyStationView(eventId: Long): List<OrderDTO>
    fun processIngredient(eventId: Long, stationId: Long, orderIngredientUpdate: OrderIngredientUpdateDTO): OrderIngredientDTO
}