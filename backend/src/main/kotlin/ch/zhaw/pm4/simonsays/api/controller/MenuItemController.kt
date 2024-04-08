package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.service.MenuItemService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("rest-api/v1/menuItem")
class MenuItemController(private val menuItemService: MenuItemService) {
    @Operation(summary = "Read all menu items")
    @GetMapping("{eventId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getMenuItems(@PathVariable("eventId") eventId: Long): List<MenuItemDTO> {
        return  menuItemService.getMenuItems(eventId)
    }
    @Operation(summary = "Retrieve a single menu item")
    @GetMapping("{menuItemId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getMenuItem(@PathVariable("menuItemId") menuItemID: Long ): MenuItemDTO {
        return menuItemService.getMenuItem(menuItemID)
    }
    @Operation(summary = "Update/Create a menu item")
    @PutMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun putMenuItem(@Valid @RequestBody request: MenuItemCreateUpdateDTO): MenuItemDTO {
        return menuItemService.createUpdateMenuItem(request)
    }
    @Operation(summary = "Delete a menu item")
    @DeleteMapping("{menuItemId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun deleteMenuItem(@PathVariable("menuItemId") menuItemId: Long) {
        menuItemService.deleteMenuItem(menuItemId)
    }
}