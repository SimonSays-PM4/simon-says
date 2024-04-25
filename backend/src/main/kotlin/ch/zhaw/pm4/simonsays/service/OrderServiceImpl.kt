package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.OrderMapper
import ch.zhaw.pm4.simonsays.api.types.*
import ch.zhaw.pm4.simonsays.entity.OrderIngredient
import ch.zhaw.pm4.simonsays.entity.OrderMenu
import ch.zhaw.pm4.simonsays.entity.OrderMenuItem
import ch.zhaw.pm4.simonsays.entity.State
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
        // TODO verfiy items with eventid and item id
        // TODO handle menuitem und menu not emtpy
        // TODO check table number between one and given on event
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

    override fun getOrderIngredientByIngredientIds(ingredientIds: List<Long>): List<OrderIngredient> {
        return orderIngredientRepository.findAllByIngredientIdInAndStateEquals(ingredientIds, State.IN_PROGRESS)
    }

    override fun updateOrderIngredientState(orderIngredientId: Long): OrderIngredientDTO {
        val orderIngredient = orderIngredientRepository.findById(orderIngredientId).orElseThrow {
            ResourceNotFoundException("OrderIngredient not found with ID: $orderIngredientId")
        }
        orderIngredient.state = State.DONE
        val savedOrderIngredient = orderIngredientRepository.save(orderIngredient)
        return orderMapper.mapOrderIngredientToOrderIngredientDTO(savedOrderIngredient)
    }

    override fun updateOrderMenuItemState(orderMenuItemId: Long): OrderMenuItemDTO {
        val orderMenuItem = orderMenuItemRepository.findById(orderMenuItemId).orElseThrow {
            ResourceNotFoundException("OrderMenuItem not found with ID: $orderMenuItemId")
        }
        orderMenuItem.state = State.DONE
        val savedOrderMenuItem = orderMenuItemRepository.save(orderMenuItem)
        checkAndUpdateOrderStateIfNeeded(savedOrderMenuItem.order!!.id!!)
        return orderMapper.mapOrderMenuItemToOrderMenuItemDTO(savedOrderMenuItem)
    }

    override fun updateOrderMenuState(orderMenuId: Long): OrderMenuDTO {
        val orderMenu = orderMenuRepository.findById(orderMenuId).orElseThrow {
            ResourceNotFoundException("OrderMenu not found with ID: $orderMenuId")
        }
        orderMenu.state = State.DONE
        val savedOrderMenu = orderMenuRepository.save(orderMenu)
        checkAndUpdateOrderStateIfNeeded(savedOrderMenu.order!!.id!!)
        return orderMapper.mapOrderMenuToOrderMenuDTO(savedOrderMenu)
    }

    private fun prepareMenuItemForSave(menuItem: MenuItemDTO, event: EventDTO): OrderMenuItem {
        val menuItemToSave = orderMapper.mapMenuItemDtoToOrderMenuItem(menuItem, event, menuItemRepository.findByIdAndEventId(menuItem.id, event.id!!).get())
        menuItem.ingredients.forEach { ingredient ->
            menuItemToSave.addOrderIngredient(orderMapper.mapIngredientDtoToOrderIngredient(ingredient, event, ingredientRepository.findByIdAndEventId(ingredient.id, event.id).get()))
        }
        return menuItemToSave
    }

    private fun checkAndUpdateOrderStateIfNeeded(orderId: Long) {
        val order = orderRepository.findById(orderId).orElseThrow {
            ResourceNotFoundException("Order not found with ID: $orderId")
        }
        val hasOpenItem = (order.menuItems?.any { menuItem -> menuItem.state != State.DONE } ?: false)
        || (order.menus?.any { menu -> menu.state != State.DONE } ?: false)

        if(!hasOpenItem) {
            order.state = State.DONE
            orderRepository.save(order)
        }
    }
}