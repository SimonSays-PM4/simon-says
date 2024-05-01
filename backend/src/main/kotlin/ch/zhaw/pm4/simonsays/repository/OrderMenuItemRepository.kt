package ch.zhaw.pm4.simonsays.repository

import ch.zhaw.pm4.simonsays.entity.FoodOrder
import ch.zhaw.pm4.simonsays.entity.OrderMenu
import ch.zhaw.pm4.simonsays.entity.OrderMenuItem
import ch.zhaw.pm4.simonsays.entity.State
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface OrderMenuItemRepository: JpaRepository<OrderMenuItem, Long> {
    fun findByIdAndEventId(id: Long, eventId: Long): Optional<OrderMenuItem>
    fun findAllByStateEqualsAndOrder_IdEqualsAndOrderMenuEquals(state: State, orderId: Long, orderMenu: OrderMenu?): MutableList<OrderMenuItem>
}