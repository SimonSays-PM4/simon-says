package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.OrderCreateDTO
import ch.zhaw.pm4.simonsays.api.types.OrderDTO

interface OrderService {
    fun createOrder(order: OrderCreateDTO, eventId: Long): OrderDTO
    fun listOrders(eventId: Long): List<OrderCreateDTO>
    fun deleteOrder(orderId: Long, eventId: Long)
}