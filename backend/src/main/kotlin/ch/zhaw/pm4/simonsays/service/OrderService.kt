package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.*

interface OrderService {
    fun createOrder(order: OrderCreateDTO, eventId: Long): OrderDTO
    fun listOrders(eventId: Long): List<OrderDTO>
    fun deleteOrder(orderId: Long, eventId: Long)
    fun getOrderIngredientByIngredientId(ingredientId: Long): List<OrderIngredientDTO>
    fun updateOrderIngredientState(orderIngredientId: Long): OrderIngredientDTO
    fun updateOrderMenuItemState(orderMenuItemId: Long): OrderMenuItemDTO
    fun updateOrderMenuState(orderMenuId: Long): OrderMenuDTO
}