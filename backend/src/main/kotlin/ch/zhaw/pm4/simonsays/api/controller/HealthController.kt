package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.service.HealthServiceImpl
import ch.zhaw.pm4.simonsays.api.types.HealthDTO
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api/health")
class HealthController (
    private val healthService: HealthServiceImpl
) {
    @Operation(summary = "shows health")
    @GetMapping("")
    fun health(): HealthDTO {
        return healthService.showHealth()
    }
}