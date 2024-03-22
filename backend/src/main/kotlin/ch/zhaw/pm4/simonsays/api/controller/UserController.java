package ch.zhaw.pm4.simonsays.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class UserController {
    @Operation(summary = "shows health")
    @GetMapping("")
    fun health(): HealthDTO {
        return healthService.showHealth()
    }
}
