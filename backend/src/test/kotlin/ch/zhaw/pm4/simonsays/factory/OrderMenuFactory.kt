package ch.zhaw.pm4.simonsays.factory

import ch.zhaw.pm4.simonsays.entity.FoodOrder
import ch.zhaw.pm4.simonsays.entity.OrderMenu
import ch.zhaw.pm4.simonsays.entity.OrderMenuItem
import ch.zhaw.pm4.simonsays.entity.State
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.repository.MenuRepository
import ch.zhaw.pm4.simonsays.repository.OrderMenuRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderMenuFactory(
        @Autowired private val menuRepository: MenuRepository,
        @Autowired private val eventRepository: EventRepository,
        @Autowired private val orderMenuRepository: OrderMenuRepository,
        @Autowired private val orderFactory: FoodOrderFactory
) {
    fun createOrderMenu(
            name: String = "Test order menu",
            eventId: Long = 1,
            menuId: Long = 1,
            orderMenuItems: MutableList<OrderMenuItem>,
            order: FoodOrder = orderFactory.createOrder(),
            state: State = State.IN_PROGRESS,
            price: Double = 0.0
    ): OrderMenu {
        val event = eventRepository.findById(eventId).orElse(null)
        val menu = menuRepository.findById(menuId).orElse(null)
        val orderMenu = OrderMenu(
            name = name,
            event = event,
            menu = menu,
            orderMenuItems = orderMenuItems,
            order = order,
            state = state,
            price = price
        )
        return orderMenuRepository.save(orderMenu)
    }
}