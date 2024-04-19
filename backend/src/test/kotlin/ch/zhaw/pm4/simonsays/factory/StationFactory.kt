package ch.zhaw.pm4.simonsays.factory

import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.Station
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.repository.StationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class StationFactory(
        @Autowired private val stationRepository: StationRepository,
        @Autowired private val eventRepository: EventRepository
) {
    fun createStation(
            name: String = "Default Station Name",
            assemblyStation: Boolean = false,
            eventId: Long = 1,
            ingredients: List<Ingredient> = listOf()
    ): Station {
        val event = eventRepository.findById(eventId).orElse(null) // Fetch the Event by ID
        val station = Station(name = name, assemblyStation = assemblyStation, event = event, ingredients = ingredients) // Use the Event entity
        return stationRepository.save(station)
    }
}