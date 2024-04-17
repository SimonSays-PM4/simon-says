package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.config.AdminEndpoint
import ch.zhaw.pm4.simonsays.service.MenuItemService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("rest-api/v1/event/{eventId}/menuitem")
class MenuItemController(private val menuItemService: MenuItemService) {
    @Operation(summary = "Read all menu items", security = [SecurityRequirement(name = "basicAuth")])
    @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @AdminEndpoint
    fun getMenuItems(@PathVariable("eventId") eventId: Long): List<MenuItemDTO> {
        return  menuItemService.listMenuItems(eventId)
    }
    @Operation(summary = "Retrieve a single menu item", security = [SecurityRequirement(name = "basicAuth")])
    @GetMapping("{menuItemId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @AdminEndpoint
    fun getMenuItem(@PathVariable("eventId") eventId: Long, @PathVariable("menuItemId") menuItemID: Long ): MenuItemDTO {
        return menuItemService.getMenuItem(menuItemID, eventId)
    }
    @Operation(summary = "Update/Create a menu item", security = [SecurityRequirement(name = "basicAuth")])
    @PutMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @AdminEndpoint
    fun putMenuItem(@PathVariable("eventId") eventId: Long, @Valid @RequestBody request: MenuItemCreateUpdateDTO): MenuItemDTO {
        return menuItemService.createUpdateMenuItem(request, eventId)
    }
    @Operation(summary = "Delete a menu item", security = [SecurityRequirement(name = "basicAuth")])
    @DeleteMapping("{menuItemId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @AdminEndpoint
    fun deleteMenuItem(@PathVariable("eventId") eventId: Long, @PathVariable("menuItemId") menuItemId: Long) {
        menuItemService.deleteMenuItem(menuItemId,eventId)
    }
}