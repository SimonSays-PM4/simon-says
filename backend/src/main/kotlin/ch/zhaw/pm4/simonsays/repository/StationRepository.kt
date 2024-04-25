package ch.zhaw.pm4.simonsays.repository

import ch.zhaw.pm4.simonsays.entity.Station
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StationRepository : JpaRepository<Station, Long> {
    fun findAllByEventId(eventId: Long): List<Station>
    fun findByIdAndEventId(id: Long, eventId: Long): Optional<Station>
    fun findByEventIdAndAssemblyStation(eventId: Long, assemblyStation: Boolean) : Optional<Station>

}