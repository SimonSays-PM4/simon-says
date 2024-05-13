package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.entity.OrderMenu
import ch.zhaw.pm4.simonsays.entity.State
import ch.zhaw.pm4.simonsays.repository.OrderMenuRepository
import org.springframework.stereotype.Service

@Service
class OrderMenuService(
    private val orderMenuRepository: OrderMenuRepository,
) {
    fun getOrderMenus(eventId: Long, orderId: Long): MutableList<OrderMenu> {
        val menus = orderMenuRepository.findAllByStateEqualsAndOrderIdEquals(State.IN_PROGRESS, orderId)
        val processedMenus: MutableList<OrderMenu> = mutableListOf()
        menus.forEach { orderMenu ->
            var menuReady = true
            orderMenu.orderMenuItems.forEach {orderMenuItem ->
                orderMenuItem.orderIngredients.forEach { orderIngredient ->
                    if(orderIngredient.state != State.DONE) {
                        menuReady = false
                    }
                }
            }

            if(menuReady) {
                processedMenus.add(orderMenu)
            }

        }

        return processedMenus
    }

}