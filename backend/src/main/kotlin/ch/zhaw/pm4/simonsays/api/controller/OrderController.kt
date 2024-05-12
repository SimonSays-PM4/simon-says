package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.types.*
import ch.zhaw.pm4.simonsays.service.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("rest-api/v1/event/{eventId}/order")
class OrderController(
    private val orderService: OrderService,
    private val orderStateService: OrderStateService
) {
    @Operation(summary = "Update/Create an order", security = [SecurityRequirement(name = "basicAuth")])
    @PutMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun putOrder(@PathVariable("eventId") eventId: Long, @Valid @RequestBody request: OrderCreateDTO): OrderDTO {
        return orderService.createOrder(request, eventId)
    }

    @Operation(summary = "get orders", security = [SecurityRequirement(name = "basicAuth")])
    @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getOrders(@PathVariable("eventId") eventId: Long): List<OrderDTO> {
        return orderService.listOrders(eventId)
    }

    @Operation(summary = "delete an order", security = [SecurityRequirement(name = "basicAuth")])
    @DeleteMapping("{orderId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteOrder(@PathVariable("eventId") eventId: Long, @PathVariable("orderId") orderId: Long){
        orderService.deleteOrder(orderId, eventId)
    }

    @Operation(summary = "update order ingredient state", security = [SecurityRequirement(name = "basicAuth")])
    @PutMapping("ingredient/{orderIngredientId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun updateOrderIngredientState(@PathVariable("eventId") eventId: Long, @PathVariable("orderIngredientId") orderIngredientId: Long): OrderIngredientDTO {
        return orderStateService.updateOrderIngredientState(eventId, orderIngredientId)
    }

    @Operation(summary = "update order menu item state", security = [SecurityRequirement(name = "basicAuth")])
    @PutMapping("menuitem/{orderMenuItemId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun updateOrderMenuItemState(@PathVariable("eventId") eventId: Long, @PathVariable("orderMenuItemId") orderMenuItemId: Long): OrderMenuItemDTO {
        return orderStateService.updateOrderMenuItemState(eventId, orderMenuItemId)
    }

    @Operation(summary = "update order menu state", security = [SecurityRequirement(name = "basicAuth")])
    @PutMapping("menu/{orderMenuId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun updateOrderMenuState(@PathVariable("eventId") eventId: Long, @PathVariable("orderMenuId") orderMenuId: Long): OrderMenuDTO {
        return orderStateService.updateOrderMenuState(eventId, orderMenuId)
    }

}