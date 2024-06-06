package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.controller.AssemblyViewNamespace
import ch.zhaw.pm4.simonsays.api.controller.StationViewNamespace
import ch.zhaw.pm4.simonsays.api.mapper.OrderMapper
import ch.zhaw.pm4.simonsays.api.types.*
import ch.zhaw.pm4.simonsays.entity.*
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.exception.ValidationException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import ch.zhaw.pm4.simonsays.repository.MenuRepository
import ch.zhaw.pm4.simonsays.repository.OrderRepository
import ch.zhaw.pm4.simonsays.service.printer.PrinterService
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderMapper: OrderMapper,
    private val orderRepository: OrderRepository,
    private val eventService: EventService,
    private val ingredientRepository: IngredientRepository,
    private val menuItemRepository: MenuItemRepository,
    private val menuRepository: MenuRepository,
    private val printerService: PrinterService,
    private val stationViewNamespace: StationViewNamespace,
    private val assemblyViewNamespace: AssemblyViewNamespace
) {
    fun createOrder(order: OrderCreateDTO, eventId: Long): OrderDTO {
        val event = eventService.getEvent(eventId)

        validateTableNumber(order, event)
        validateOrderHasItems(order)

        val savedOrder = orderRepository.save(prepareOrderForSave(order, event))
        assemblyViewNamespace.onChange(orderMapper.mapOrderToOrderDTO(savedOrder))
        if(savedOrder.menus != null && savedOrder.menus!!.isNotEmpty()) {
            savedOrder.menus!!.forEach { menu ->
                menu.orderMenuItems.forEach { menuItem ->
                    menuItem.orderIngredients.forEach { orderIngredient ->
                        if(orderIngredient.ingredient.mustBeProduced) {
                            stationViewNamespace.onChange(orderMapper.mapOrderIngredientToOrderIngredientDTO(orderIngredient))
                        }
                    }
                }
            }
        }

        if(savedOrder.menuItems != null && savedOrder.menuItems!!.isNotEmpty()) {
            savedOrder.menuItems!!.forEach { menuItem ->
                menuItem.orderIngredients.forEach { orderIngredient ->
                    if(orderIngredient.ingredient.mustBeProduced) {
                        stationViewNamespace.onChange(orderMapper.mapOrderIngredientToOrderIngredientDTO(orderIngredient))
                    }
                }
            }
        }

        printerService.printFoodOrder(savedOrder)
        return orderMapper.mapOrderToOrderDTO(savedOrder)
    }

    fun listOrders(eventId: Long): List<OrderDTO> {
        return orderRepository.findAllByEventId(eventId).map { order ->
            orderMapper.mapOrderToOrderDTO(order)
        }
    }

    fun getOrder(orderId: Long): FoodOrder{
        val order = orderRepository.findById(orderId).orElseThrow {
            ResourceNotFoundException("Order not found with ID: $orderId")
        }
        return order
    }

    fun getEventIdForOrder(orderId: Long): Long? {
        return orderRepository.findEventIdByOrderId(orderId)
    }

    fun deleteOrder(orderId: Long, eventId: Long) {
        val order = orderRepository.findByIdAndEventId(orderId, eventId).orElseThrow {
            ResourceNotFoundException("Order not found with ID: $orderId")
        }
        orderRepository.delete(order)
    }

    private fun prepareMenuItemForSave(menuItem: MenuItemDTO, menuItems: List<MenuItem>, ingredients: List<Ingredient>, event: EventDTO): OrderMenuItem {
        val menuItemToSave = orderMapper.mapMenuItemDtoToOrderMenuItem(
            menuItem,
            event,
            menuItems.find { it.id == menuItem.id } ?: throw ResourceNotFoundException("MenuItem not found with ID: ${menuItem.id}")
        )
        menuItem.ingredients.forEach { ingredient ->
            val originalIngredient: Ingredient = ingredients.find { it.id == ingredient.id } ?: throw ResourceNotFoundException("Ingredient not found with ID: ${ingredient.id}")
            menuItemToSave.addOrderIngredient(
                orderMapper.mapIngredientDtoToOrderIngredient(
                    ingredient,
                    event,
                    originalIngredient,
                    if(originalIngredient.mustBeProduced) State.IN_PROGRESS else State.DONE
                )
            )
        }
        return menuItemToSave
    }

    private fun validateTableNumber(order: OrderCreateDTO, event: EventDTO) {
        if (!order.isTakeAway!! && (order.tableNumber == null || order.tableNumber!! < 1 || order.tableNumber!! > event.numberOfTables)) {
            throw ValidationException("Table number must be between 1 and ${event.numberOfTables}")
        }
    }

    private fun validateOrderHasItems(order: OrderCreateDTO) {
        if (order.menuItems.isNullOrEmpty() && order.menus.isNullOrEmpty()) {
            throw ValidationException("Order must have at least one menu or menu item")
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

    private fun prepareOrderForSave(order: OrderCreateDTO, event: EventDTO): FoodOrder{
        val menus = menuRepository.findAllByEventId(event.id!!)
        val menuItems = menuItemRepository.findAllByEventId(event.id)
        val ingredients = ingredientRepository.findAllByEventId(event.id)
        var totalPrice = 0.0

        val orderToSave = orderMapper.mapOrderDtoToOrder(order, event, listOf(), listOf(), totalPrice)
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
        return orderToSave
    }
}