package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.types.OrderCreateDTO
import ch.zhaw.pm4.simonsays.api.types.OrderDTO
import ch.zhaw.pm4.simonsays.config.AdminEndpoint
import ch.zhaw.pm4.simonsays.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("rest-api/v1/event/{eventId}/order")
class OrderController(
    private val orderService: OrderService
) {
    @Operation(summary = "Update/Create an order", security = [SecurityRequirement(name = "basicAuth")])
    @PutMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @AdminEndpoint
    fun putOrder(@PathVariable("eventId") eventId: Long, @Valid @RequestBody request: OrderCreateDTO): OrderDTO {
        return orderService.createOrder(request, eventId)
    }

    @Operation(summary = "get orders", security = [SecurityRequirement(name = "basicAuth")])
    @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @AdminEndpoint
    fun getOrders(@PathVariable("eventId") eventId: Long): List<OrderDTO> {
        return orderService.listOrders(eventId)
    }

    @Operation(summary = "delete an order", security = [SecurityRequirement(name = "basicAuth")])
    @DeleteMapping("{orderId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @AdminEndpoint
    fun deleteOrder(@PathVariable("eventId") eventId: Long, @PathVariable("orderId") orderId: Long){
        orderService.deleteOrder(orderId, eventId)
    }
}