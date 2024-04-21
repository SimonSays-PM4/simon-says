package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.OrderMapper
import ch.zhaw.pm4.simonsays.api.types.OrderCreateDTO
import ch.zhaw.pm4.simonsays.api.types.OrderDTO
import ch.zhaw.pm4.simonsays.entity.OrderIngredient
import ch.zhaw.pm4.simonsays.entity.OrderMenu
import ch.zhaw.pm4.simonsays.entity.OrderMenuItem
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.*
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

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
        val orderToSave = orderMapper.mapOrderDtoToOrder(order, event, menus, setOf(), 0.0)
        order.menus.forEach { menu ->
            val menuItems = mutableSetOf<OrderMenuItem>()
            menu.menuItems.forEach { menuItem ->
                val ingredients = mutableSetOf<OrderIngredient>()
                menuItem.ingredients.forEach { ingredient ->
                    ingredients.add(orderMapper.mapIngredientDtoToOrderIngredient(ingredient, event, ingredientRepository.findByIdAndEventId(ingredient.id, eventId).get()))
                }
                menuItems.add(orderMapper.mapMenuItemDtoToOrderMenuItem(menuItem, event, menuItemRepository.findByIdAndEventId(menuItem.id, eventId).get(), ingredients))
            }
            orderToSave.addMenu((orderMapper.mapMenuDtoToOrderMenu(menu, event,menuRepository.findByIdAndEventId(menu.id, eventId).get(), menuItems)))
        }


        val savedOrder = orderRepository.save(orderToSave)
        return orderMapper.mapOrderToOrderDTO(savedOrder)
    }

    override fun listOrders(eventId: Long): List<OrderCreateDTO> {
        return listOf()
    }

    override fun deleteOrder(orderId: Long, eventId: Long) {
        TODO("Not yet implemented")
    }
}