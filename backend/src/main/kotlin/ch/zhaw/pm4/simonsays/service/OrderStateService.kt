package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.controller.AssemblyViewNamespace
import ch.zhaw.pm4.simonsays.api.controller.StationViewNamespace
import ch.zhaw.pm4.simonsays.api.mapper.OrderMapper
import ch.zhaw.pm4.simonsays.api.types.OrderIngredientDTO
import ch.zhaw.pm4.simonsays.api.types.OrderIngredientUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.OrderMenuDTO
import ch.zhaw.pm4.simonsays.api.types.OrderMenuItemDTO
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.OrderIngredient
import ch.zhaw.pm4.simonsays.entity.State
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.exception.ValidationException
import ch.zhaw.pm4.simonsays.repository.*
import org.springframework.stereotype.Service

@Service
class OrderStateService(
        private val ingredientRepository: IngredientRepository,
        private val orderRepository: OrderRepository,
        private val orderMenuItemRepository: OrderMenuItemRepository,
        private val orderMenuRepository: OrderMenuRepository,
        private val orderIngredientRepository: OrderIngredientRepository,
        private val assemblyViewNamespace: AssemblyViewNamespace,
        private val stationViewNamespace: StationViewNamespace,
        private val orderMapper: OrderMapper,
) {

    fun processIngredient(eventId: Long, stationId: Long, orderIngredientUpdate: OrderIngredientUpdateDTO): OrderIngredientDTO {
        val stationIngredients: List<Ingredient> = ingredientRepository.findAllByStationsIdAndEventId(stationId, eventId)
        val stationIngredientIds: List<Long> = stationIngredients.map { it.id!! }
        val orderIngredient: OrderIngredient = orderIngredientRepository.findByIdAndEventId(orderIngredientUpdate.id, eventId).orElseThrow() {
            ResourceNotFoundException("No order ingredient found with the ID: ${orderIngredientUpdate.id}")
        }
        if(!stationIngredientIds.contains(orderIngredient.ingredient.id)) {
            throw ValidationException(
                    "This station is not allowed to update the state of the ingredient with the id: ${orderIngredient.id} (${orderIngredient.ingredient.name})"
            )
        }
        return updateOrderIngredientState(eventId, orderIngredient.id!!)
    }

    fun updateOrderMenuItemState(eventId: Long, orderMenuItemId: Long): OrderMenuItemDTO {
        val orderMenuItem = orderMenuItemRepository.findByIdAndEventId(orderMenuItemId, eventId).orElseThrow {
            ResourceNotFoundException("OrderMenuItem not found with ID: $orderMenuItemId")
        }
        orderMenuItem.state = State.DONE
        val savedOrderMenuItem = orderMenuItemRepository.save(orderMenuItem)
        assemblyViewNamespace.onChange(orderMapper.mapOrderToOrderDTO(savedOrderMenuItem.order!!))
        checkAndUpdateOrderStateIfNeeded(savedOrderMenuItem.order!!.id!!)
        return orderMapper.mapOrderMenuItemToOrderMenuItemDTO(savedOrderMenuItem)
    }

    fun updateOrderIngredientState(eventId: Long, orderIngredientId: Long): OrderIngredientDTO {
        val orderIngredient = orderIngredientRepository.findByIdAndEventId(orderIngredientId, eventId).orElseThrow {
            ResourceNotFoundException("OrderIngredient not found with ID: $orderIngredientId")
        }
        orderIngredient.state = State.DONE
        val savedOrderIngredient = orderIngredientRepository.save(orderIngredient)
        stationViewNamespace.onChange(orderMapper.mapOrderIngredientToOrderIngredientDTO(orderIngredient))
        return orderMapper.mapOrderIngredientToOrderIngredientDTO(savedOrderIngredient)
    }

    fun updateOrderMenuState(eventId: Long, orderMenuId: Long): OrderMenuDTO {
        val orderMenu = orderMenuRepository.findByIdAndEventId(orderMenuId, eventId).orElseThrow {
            ResourceNotFoundException("OrderMenu not found with ID: $orderMenuId")
        }
        orderMenu.state = State.DONE
        val savedOrderMenu = orderMenuRepository.save(orderMenu)
        assemblyViewNamespace.onChange(orderMapper.mapOrderToOrderDTO(savedOrderMenu.order!!))
        checkAndUpdateOrderStateIfNeeded(savedOrderMenu.order!!.id!!)
        return orderMapper.mapOrderMenuToOrderMenuDTO(savedOrderMenu)
    }

    private fun checkAndUpdateOrderStateIfNeeded(orderId: Long) {
        val order = orderRepository.findById(orderId).orElseThrow {
            ResourceNotFoundException("Order not found with ID: $orderId")
        }
        val hasOpenItem = (order.menuItems?.any { menuItem -> menuItem.state != State.DONE } ?: false)
                || (order.menus?.any { menu -> menu.state != State.DONE } ?: false)

        if (!hasOpenItem) {
            order.state = State.DONE
            val updatedOrder = orderRepository.save(order)
            assemblyViewNamespace.onChange(orderMapper.mapOrderToOrderDTO(updatedOrder))
        }
    }


}