package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.types.MenuCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuDTO
import ch.zhaw.pm4.simonsays.config.AdminEndpoint
import ch.zhaw.pm4.simonsays.service.MenuService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("rest-api/v1/event/{eventId}/menu")
class MenuController (private  val menuService: MenuService){
    @Operation(summary = "Read all menus", security = [SecurityRequirement(name = "basicAuth")])
    @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @AdminEndpoint
    fun getMenus(@PathVariable("eventId") eventId: Long): List<MenuDTO> {
        return  menuService.listMenus(eventId)
    }
    @Operation(summary = "Retrieve a single menu", security = [SecurityRequirement(name = "basicAuth")])
    @GetMapping("{menuId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @AdminEndpoint
    fun getMenu(@PathVariable("eventId") eventId: Long, @PathVariable("menuId") menuId: Long ): MenuDTO {
        return menuService.getMenu(menuId, eventId)
    }
    @Operation(summary = "Update/Create a menu", security = [SecurityRequirement(name = "basicAuth")])
    @PutMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @AdminEndpoint
    fun putMenu(@PathVariable("eventId") eventId: Long, @Valid @RequestBody request: MenuCreateUpdateDTO): MenuDTO {
        return menuService.createUpdateMenu(request, eventId)
    }
    @Operation(summary = "Delete a menu", security = [SecurityRequirement(name = "basicAuth")])
    @DeleteMapping("{menuId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @AdminEndpoint
    fun deleteMenu(@PathVariable("eventId") eventId: Long, @PathVariable("menuId") menuId: Long) {
        menuService.deleteMenu(menuId,eventId)
    }
}