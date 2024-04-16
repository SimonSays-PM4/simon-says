package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.types.EventCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.config.AdminEndpoint
import ch.zhaw.pm4.simonsays.service.EventService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("rest-api/v1/event")
class EventController(private val eventService: EventService) {
    @Operation(summary = "Read all events", security = [SecurityRequirement(name = "basicAuth")])
    @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @AdminEndpoint
    fun getEvents(): List<EventDTO> {
        return  eventService.getEvents()
    }
    @Operation(summary = "Retrieve a single event", security = [SecurityRequirement(name = "basicAuth")])
    @GetMapping("{eventId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getEvent(@PathVariable("eventId") eventId: Long ): EventDTO {
        return eventService.getEvent(eventId)
    }
    @Operation(summary = "Update/Create an event", security = [SecurityRequirement(name = "basicAuth")])
    @PutMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @AdminEndpoint
    fun putEvent(@Valid @RequestBody request: EventCreateUpdateDTO): EventDTO {
        return eventService.createUpdateEvent(request)
    }
    @Operation(summary = "Delete an event", security = [SecurityRequirement(name = "basicAuth")])
    @DeleteMapping("{eventId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @AdminEndpoint
    fun deleteEvent(@PathVariable("eventId") eventId: Long) {
        eventService.deleteEvent(eventId)
    }
}
