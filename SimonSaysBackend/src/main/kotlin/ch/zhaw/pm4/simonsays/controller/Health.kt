package ch.zhaw.pm4.simonsays.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api/health")
class Health {
    @Operation(summary = "shows health")
    @GetMapping("")
    fun health(): String {
        return "up"
    }
}