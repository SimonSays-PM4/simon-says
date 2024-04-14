package ch.zhaw.pm4.simonsays.repository

import ch.zhaw.pm4.simonsays.entity.Menu
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MenuRepository: JpaRepository<Menu, Long> {
    fun findAllByEventId(eventId: Long): List<Menu>
    fun findByIdAndEventId(id: Long, eventId: Long): Optional<Menu>

}