package ch.zhaw.pm4.simonsays.repository

import ch.zhaw.pm4.simonsays.entity.FoodOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderRepository: JpaRepository<FoodOrder, Long> {
    fun findAllByEventId(eventId: Long): List<FoodOrder>
    fun findByIdAndEventId(orderId: Long, eventId: Long): Optional<FoodOrder>
}