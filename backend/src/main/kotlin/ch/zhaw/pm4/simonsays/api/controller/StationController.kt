package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.types.*
import ch.zhaw.pm4.simonsays.config.AdminEndpoint
import ch.zhaw.pm4.simonsays.service.StationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("rest-api/v1/event/{eventId}/station")
class StationController(private val stationService: StationService) {
    @Operation(summary = "Read all stations", security = [SecurityRequirement(name = "basicAuth")])
    @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getStations(@PathVariable("eventId") eventId: Long): List<StationDTO> {
        return  stationService.listStations(eventId)
    }
    @Operation(summary = "Retrieve a single station", security = [SecurityRequirement(name = "basicAuth")])
    @GetMapping("{stationId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getStation(@PathVariable("eventId") eventId: Long, @PathVariable("stationId") stationId: Long ): StationDTO {
        return stationService.getStation(stationId, eventId)
    }
    @Operation(summary = "Update/Create a station", security = [SecurityRequirement(name = "basicAuth")])
    @PutMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @AdminEndpoint
    fun putStation(@PathVariable("eventId") eventId: Long, @Valid @RequestBody request: StationCreateUpdateDTO): StationDTO {
        return stationService.createUpdateStation(request, eventId)
    }
    @Operation(summary = "Delete a station", security = [SecurityRequirement(name = "basicAuth")])
    @DeleteMapping("{stationId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @AdminEndpoint
    fun deleteStation(@PathVariable("eventId") eventId: Long, @PathVariable("stationId") stationId: Long) {
        stationService.deleteStation(stationId, eventId)
    }
    @Operation(summary = "Get Station View", security = [SecurityRequirement(name = "basicAuth")])
    @GetMapping("{stationId}/view", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getStationView(@PathVariable("eventId") eventId: Long, @PathVariable("stationId") stationId: Long): List<OrderIngredientDTO> {
        return  stationService.getStationView(stationId, eventId)
    }

    @Operation(summary = "Get Assembly Station View", security = [SecurityRequirement(name = "basicAuth")])
    @GetMapping("assembly", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getAssemblyStationView(@PathVariable("eventId") eventId: Long): List<OrderDTO> {
        return  stationService.getAssemblyStationView(eventId)
    }

    @Operation(summary = "Process Ingredient at Station", security = [SecurityRequirement(name = "basicAuth")])
    @PostMapping("{stationId}/view", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun processIngredient(@PathVariable("eventId") eventId: Long, @PathVariable("stationId") stationId: Long, @Valid @RequestBody request: OrderIngredientUpdateDTO): OrderIngredientDTO {
        return stationService.processIngredient(eventId, stationId, request)
    }

}