package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.types.IngredientCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.service.IngredientService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("rest-api/v1/event/{eventId}/ingredient")
class IngredientController (
    private val ingredientService: IngredientService
) {
    @Operation(summary = "Creates new ingredient")
    @PutMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun createIngredient(@RequestBody @Valid request: IngredientCreateUpdateDTO, @PathVariable eventId: Long): IngredientDTO {
        return ingredientService.createUpdateIngredient(request, eventId)
    }
    @Operation(summary = "List of ingredients")
    @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun listIngredients(@PathVariable eventId: Long): List<IngredientDTO> {
        return ingredientService.listIngredients(eventId)
    }
    @Operation(summary = "Get ingredient")
    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getIngredient(@PathVariable id: Long, @PathVariable eventId: Long): IngredientDTO {
        return ingredientService.getIngredient(id, eventId)
    }
    @Operation(summary = "Delete ingredient")
    @DeleteMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteIngredient(@PathVariable id: Long, @PathVariable eventId: Long) {
        ingredientService.deleteIngredient(id, eventId)
    }
}