package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.types.EventCreateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.EventPutDTO
import ch.zhaw.pm4.simonsays.service.EventService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("rest-api/v1/event")
class EventController(private val eventService: EventService) {

    @Operation(summary = "Creates new event")
    @PostMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun createEvent(@RequestBody @Valid request: EventCreateDTO): EventDTO {
        return eventService.createEvent(request)
    }

    @Operation(summary = "Read all events")
    @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getEvents(): List<EventDTO> {
        return  eventService.getEvents()
    }

    @Operation(summary = "Retrieve a single event")
    @GetMapping("{eventId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getEvent(@PathVariable("eventId") eventId: Long ): EventDTO {
        return eventService.getEvent(eventId)
    }

    @Operation(summary = "Update an event")
    @PutMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun putEvent(@Valid @RequestBody request: EventPutDTO): EventDTO {
        return eventService.putEvent(request)
    }

    @Operation(summary = "Delete an event")
    @DeleteMapping("{eventId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun deleteEvent(@PathVariable("eventId") eventId: Long): EventDTO {
        return eventService.deleteEvent(eventId)
    }
}
