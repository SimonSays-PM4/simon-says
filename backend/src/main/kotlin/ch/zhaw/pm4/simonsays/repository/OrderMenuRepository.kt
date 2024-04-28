package ch.zhaw.pm4.simonsays.repository

import ch.zhaw.pm4.simonsays.entity.OrderMenu
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderMenuRepository: JpaRepository<OrderMenu, Long> {
    fun findByIdAndEventId(id: Long, eventId: Long): Optional<OrderMenu>
}