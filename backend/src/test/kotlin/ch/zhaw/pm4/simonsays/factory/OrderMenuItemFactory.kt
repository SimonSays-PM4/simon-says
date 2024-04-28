package ch.zhaw.pm4.simonsays.factory

import ch.zhaw.pm4.simonsays.entity.*
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import ch.zhaw.pm4.simonsays.repository.OrderMenuItemRepository
import ch.zhaw.pm4.simonsays.repository.OrderMenuRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderMenuItemFactory(
        @Autowired private val orderMenuItemRepository: OrderMenuItemRepository,
        @Autowired private val menuItemRepository: MenuItemRepository,
        @Autowired private val eventRepository: EventRepository,
        @Autowired private val orderMenuRepository: OrderMenuRepository,
        @Autowired private val orderFactory: FoodOrderFactory
) {
    fun createOrderMenuItem(
        name: String = "Test order menu item",
        eventId: Long = 1,
        menuItemId: Long = 1,
        orderIngredients: MutableSet<OrderIngredient>,
        price: Double = 0.0,
        orderMenuId: Long? = null,
        order: FoodOrder = orderFactory.createOrder(),
        state: State = State.IN_PROGRESS
    ): OrderMenuItem {
        val event = eventRepository.findById(eventId).orElse(null)
        val menuItem = menuItemRepository.findById(menuItemId).orElse(null)
        var orderMenu: OrderMenu? = null
        if(orderMenuId != null) {
            orderMenu = orderMenuRepository.findByIdAndEventId(orderMenuId, event.id!!).orElse(null)
        }
        val orderMenuItem = OrderMenuItem(
                name = name,
                event = event,
                menuItem = menuItem,
                orderIngredients = orderIngredients,
                price = price,
                orderMenu = orderMenu,
                order = order,
                state = state
        )
        return orderMenuItemRepository.save(orderMenuItem)
    }
}