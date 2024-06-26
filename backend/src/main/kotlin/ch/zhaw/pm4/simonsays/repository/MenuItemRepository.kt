package ch.zhaw.pm4.simonsays.repository

import ch.zhaw.pm4.simonsays.entity.MenuItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MenuItemRepository : JpaRepository<MenuItem, Long> {
    fun findAllByEventId(eventId: Long): List<MenuItem>
    fun findByIdAndEventId(id: Long, eventId: Long): Optional<MenuItem>
    fun findByIdIn(id: List<Long>): List<MenuItem>

}

