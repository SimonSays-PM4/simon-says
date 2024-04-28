package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.OrderMapper
import ch.zhaw.pm4.simonsays.api.types.*
import ch.zhaw.pm4.simonsays.entity.*
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.exception.ValidationException
import ch.zhaw.pm4.simonsays.repository.*
import org.springframework.stereotype.Service

@Service
class OrderServiceImpl(
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
) : OrderService {
    override fun createOrder(order: OrderCreateDTO, eventId: Long): OrderDTO {
        val event = eventService.getEvent(eventId)
        val menus = menuRepository.findAllByEventId(eventId)
        val menuItems = menuItemRepository.findAllByEventId(eventId)
        val ingredients = ingredientRepository.findAllByEventId(eventId)
        var totalPrice = 0.0

        validateTableNumber(order, event)

        val orderToSave = orderMapper.mapOrderDtoToOrder(order, event, setOf(), setOf(), totalPrice)
        order.menus?.forEach { menu ->
            validateMenu(menu)
            val menuToSave = orderMapper.mapMenuDtoToOrderMenu(
                menu,
                event,
                menus.find { it.id == menu.id } ?: throw ResourceNotFoundException("Menu not found with ID: ${menu.id}")
            )
            menu.menuItems.forEach { menuItem ->
                validateMenuItem(menuItem)
                menuToSave.addOrderMenuItem(prepareMenuItemForSave(menuItem, menuItems, ingredients, event))
            }
            totalPrice = totalPrice.plus(menuToSave.price)
            orderToSave.addMenu(menuToSave)
        }

        order.menuItems?.forEach { menuItem ->
            validateMenuItem(menuItem)
            val menuItemToSave = prepareMenuItemForSave(menuItem, menuItems, ingredients, event)
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

    override fun updateOrderIngredientState(eventId: Long, orderIngredientId: Long): OrderIngredientDTO {
        val orderIngredient = orderIngredientRepository.findByIdAndEventId(orderIngredientId, eventId).orElseThrow {
            ResourceNotFoundException("OrderIngredient not found with ID: $orderIngredientId")
        }
        orderIngredient.state = State.DONE
        val savedOrderIngredient = orderIngredientRepository.save(orderIngredient)
        return orderMapper.mapOrderIngredientToOrderIngredientDTO(savedOrderIngredient)
    }

    override fun updateOrderMenuItemState(eventId: Long, orderMenuItemId: Long): OrderMenuItemDTO {
        val orderMenuItem = orderMenuItemRepository.findByIdAndEventId(orderMenuItemId, eventId).orElseThrow {
            ResourceNotFoundException("OrderMenuItem not found with ID: $orderMenuItemId")
        }
        orderMenuItem.state = State.DONE
        val savedOrderMenuItem = orderMenuItemRepository.save(orderMenuItem)
        checkAndUpdateOrderStateIfNeeded(savedOrderMenuItem.order!!.id!!)
        return orderMapper.mapOrderMenuItemToOrderMenuItemDTO(savedOrderMenuItem)
    }

    override fun updateOrderMenuState(eventId: Long, orderMenuId: Long): OrderMenuDTO {
        val orderMenu = orderMenuRepository.findByIdAndEventId(orderMenuId, eventId).orElseThrow {
            ResourceNotFoundException("OrderMenu not found with ID: $orderMenuId")
        }
        orderMenu.state = State.DONE
        val savedOrderMenu = orderMenuRepository.save(orderMenu)
        checkAndUpdateOrderStateIfNeeded(savedOrderMenu.order!!.id!!)
        return orderMapper.mapOrderMenuToOrderMenuDTO(savedOrderMenu)
    }

    private fun prepareMenuItemForSave(menuItem: MenuItemDTO, menuItems: List<MenuItem>, ingredients: List<Ingredient>, event: EventDTO): OrderMenuItem {
        val menuItemToSave = orderMapper.mapMenuItemDtoToOrderMenuItem(
            menuItem,
            event,
            menuItems.find { it.id == menuItem.id } ?: throw ResourceNotFoundException("MenuItem not found with ID: ${menuItem.id}")
        )
        menuItem.ingredients.forEach { ingredient ->
            menuItemToSave.addOrderIngredient(
                orderMapper.mapIngredientDtoToOrderIngredient(
                    ingredient,
                    event,
                    ingredients.find { it.id == ingredient.id } ?: throw ResourceNotFoundException("Ingredient not found with ID: ${ingredient.id}")
                )
            )
        }
        return menuItemToSave
    }

    private fun checkAndUpdateOrderStateIfNeeded(orderId: Long) {
        val order = orderRepository.findById(orderId).orElseThrow {
            ResourceNotFoundException("Order not found with ID: $orderId")
        }
        val hasOpenItem = (order.menuItems?.any { menuItem -> menuItem.state != State.DONE } ?: false)
                || (order.menus?.any { menu -> menu.state != State.DONE } ?: false)

        if (!hasOpenItem) {
            order.state = State.DONE
            orderRepository.save(order)
        }
    }
    private fun validateTableNumber(order: OrderCreateDTO, event: EventDTO) {
        if (!order.isTakeAway!! && (order.tableNumber == null || order.tableNumber!! < 1 || order.tableNumber!! > event.numberOfTables)) {
            throw ValidationException("Table number must be between 1 and ${event.numberOfTables}")
        }
    }

    private fun validateMenuItem(menuItem: MenuItemDTO) {
        if (menuItem.ingredients.isEmpty()) {
            throw ValidationException("Menu item must have at least one ingredient")
        }
    }

    private fun validateMenu(menu: MenuDTO) {
        if (menu.menuItems.isEmpty()) {
            throw ValidationException("Menu must have at least one menu item")
        }
    }

}