package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.OrderMapper
import ch.zhaw.pm4.simonsays.api.types.OrderCreateDTO
import ch.zhaw.pm4.simonsays.api.types.OrderDTO
import ch.zhaw.pm4.simonsays.entity.OrderMenu
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
        val orderToSave = orderMapper.mapOrderDtoToOrder(order, event, menus, setOf(), 0.0)
        order.menus.forEach { menu ->
            val menuToSave = orderMapper.mapMenuDtoToOrderMenu(menu, event,menuRepository.findByIdAndEventId(menu.id, eventId).get(), setOf())
            menu.menuItems.forEach { menuItem ->
                val menuItemToSave = orderMapper.mapMenuItemDtoToOrderMenuItem(menuItem, event, menuItemRepository.findByIdAndEventId(menuItem.id, eventId).get(), setOf())
                menuItem.ingredients.forEach { ingredient ->
                    menuItemToSave.addOrderIngredient(orderMapper.mapIngredientDtoToOrderIngredient(ingredient, event, ingredientRepository.findByIdAndEventId(ingredient.id, eventId).get()))
                }
                menuToSave.addOrderMenuItem(menuItemToSave)
            }
            orderToSave.addMenu(menuToSave)
        }

        order.menuItems.forEach { menuItem ->
            val menuItemToSave = orderMapper.mapMenuItemDtoToOrderMenuItem(menuItem, event, menuItemRepository.findByIdAndEventId(menuItem.id, eventId).get(), setOf())
            menuItem.ingredients.forEach { ingredient ->
                menuItemToSave.addOrderIngredient(orderMapper.mapIngredientDtoToOrderIngredient(ingredient, event, ingredientRepository.findByIdAndEventId(ingredient.id, eventId).get()))
            }
            orderToSave.addMenuItem(menuItemToSave)
        }


        val savedOrder = orderRepository.save(orderToSave)
        return orderMapper.mapOrderToOrderDTO(savedOrder)
    }

    override fun listOrders(eventId: Long): List<OrderDTO> {
        return orderRepository.findAllByEventId(eventId).map { order ->
            orderMapper.mapOrderToOrderDTO(order)
        }
    }

    override fun deleteOrder(orderId: Long, eventId: Long) {
        TODO("Not yet implemented")
    }
}