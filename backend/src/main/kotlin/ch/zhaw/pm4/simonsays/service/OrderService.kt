package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.*
import ch.zhaw.pm4.simonsays.entity.FoodOrder
import ch.zhaw.pm4.simonsays.entity.OrderIngredient
import ch.zhaw.pm4.simonsays.entity.OrderMenu
import ch.zhaw.pm4.simonsays.entity.OrderMenuItem

interface OrderService {
    fun createOrder(order: OrderCreateDTO, eventId: Long): OrderDTO
    fun listOrders(eventId: Long): List<OrderDTO>
    fun deleteOrder(orderId: Long, eventId: Long)
    fun getOrderIngredientByIngredientIds(ingredientIds: List<Long>): List<OrderIngredient>
    fun updateOrderIngredientState(eventId: Long, orderIngredientId: Long): OrderIngredientDTO
    fun updateOrderMenuItemState(eventId: Long, orderMenuItemId: Long): OrderMenuItemDTO
    fun updateOrderMenuState(eventId: Long, orderMenuId: Long): OrderMenuDTO
    fun getOrderMenuItems(eventId: Long, orderId: Long): MutableList<OrderMenuItem>
    fun getOrderMenus(eventId: Long, orderId: Long): MutableList<OrderMenu>
}