package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.service.HealthService
import ch.zhaw.pm4.simonsays.api.types.HealthDTO
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("rest-api/v1/health")
class HealthController(
    private val healthService: HealthService
) {
    @Operation(summary = "shows health")
    @GetMapping("")
    fun health(): HealthDTO {
        return healthService.showHealth()
    }
}