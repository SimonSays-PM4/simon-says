package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.entity.OrderMenuItem
import ch.zhaw.pm4.simonsays.entity.State
import ch.zhaw.pm4.simonsays.repository.OrderMenuItemRepository
import org.springframework.stereotype.Service

@Service
class OrderMenuItemService(
        private val orderMenuItemRepository: OrderMenuItemRepository,
) {

    fun getOrderMenuItems(eventId: Long, orderId: Long): MutableList<OrderMenuItem> {
        val menuItems: MutableList<OrderMenuItem> = orderMenuItemRepository.findAllByStateEqualsAndOrderIdEqualsAndOrderMenuEquals(State.IN_PROGRESS, orderId, null)
        val processedMenuItems: MutableList<OrderMenuItem> = mutableListOf()
        menuItems.forEach { orderMenuItem ->
            var allIngredientsComplete = true
            orderMenuItem.orderIngredients.forEach { orderIngredient ->
                if(orderIngredient.state != State.DONE) {
                    allIngredientsComplete = false
                }
            }
            if(allIngredientsComplete) {
                processedMenuItems.add(orderMenuItem)
            }
        }
        return processedMenuItems
    }

}