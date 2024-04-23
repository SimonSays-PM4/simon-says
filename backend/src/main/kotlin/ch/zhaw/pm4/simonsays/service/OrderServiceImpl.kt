package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.OrderMapper
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.api.types.OrderCreateDTO
import ch.zhaw.pm4.simonsays.api.types.OrderDTO
import ch.zhaw.pm4.simonsays.entity.OrderMenu
import ch.zhaw.pm4.simonsays.entity.OrderMenuItem
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.*
import org.springframework.stereotype.Service

@Service
class OrderServiceImpl (
    private val orderMapper: OrderMapper,
    private val orderRepository: OrderRepository,
    private val orderIngredientRepository: OrderIngredientRepository,
    private val orderMenuRepository: OrderMenuRepository,
    private val orderMenuItemRepository: OrderMenuItemRepository,
    private val eventService: EventService,
    private val ingredientService: IngredientService,
    private val ingredientRepository: IngredientRepository,
    private val menuItemRepository: MenuItemRepository,
    private val menuRepository: MenuRepository
): OrderService {
    override fun createOrder(order: OrderCreateDTO, eventId: Long): OrderDTO {
        val event = eventService.getEvent(eventId)
        val menus = mutableSetOf<OrderMenu>()
        var totalPrice = 0.0
        val orderToSave = orderMapper.mapOrderDtoToOrder(order, event, menus, setOf(), totalPrice)
        order.menus.forEach { menu ->
            val menuToSave = orderMapper.mapMenuDtoToOrderMenu(menu, event,menuRepository.findByIdAndEventId(menu.id, eventId).get())
            menu.menuItems.forEach { menuItem ->
                val menuItemToSave = prepareMenuItemForSave(menuItem, event)
                menuToSave.addOrderMenuItem(menuItemToSave)
            }
            totalPrice = totalPrice.plus(menuToSave.price)
            orderToSave.addMenu(menuToSave)
        }

        order.menuItems.forEach { menuItem ->
            val menuItemToSave = prepareMenuItemForSave(menuItem, event)
            totalPrice = totalPrice.plus(menuItemToSave.price)
            orderToSave.addMenuItem(menuItemToSave)
        }

        orderToSave.totalPrice = totalPrice
        val savedOrder = orderRepository.save(orderToSave)
        return orderMapper.mapOrderToOrderDTO(savedOrder)
    }

    override fun listOrders(eventId: Long): List<OrderDTO> {
        return orderRepository.findAllByEventId(eventId).map { order ->
            orderMapper.mapOrderToOrderDTO(order)
        }
    }

    override fun deleteOrder(orderId: Long, eventId: Long) {
        val order = orderRepository.findByIdAndEventId(orderId, eventId).orElseThrow {
            ResourceNotFoundException("Order not found with ID: $orderId")
        }
        orderRepository.delete(order)
    }

    private fun prepareMenuItemForSave(menuItem: MenuItemDTO, event: EventDTO): OrderMenuItem {
        val menuItemToSave = orderMapper.mapMenuItemDtoToOrderMenuItem(menuItem, event, menuItemRepository.findByIdAndEventId(menuItem.id, event.id!!).get())
        menuItem.ingredients.forEach { ingredient ->
            menuItemToSave.addOrderIngredient(orderMapper.mapIngredientDtoToOrderIngredient(ingredient, event, ingredientRepository.findByIdAndEventId(ingredient.id, event.id).get()))
        }
        return menuItemToSave
    }
}