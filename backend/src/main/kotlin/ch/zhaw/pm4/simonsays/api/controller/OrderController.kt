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
    @Operation(summary = "Update/Create a order", security = [SecurityRequirement(name = "basicAuth")])
    @PutMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @AdminEndpoint
    fun putMenu(@PathVariable("eventId") eventId: Long, @Valid @RequestBody request: OrderCreateDTO): OrderDTO {
        return orderService.createOrder(request, eventId)
    }
}