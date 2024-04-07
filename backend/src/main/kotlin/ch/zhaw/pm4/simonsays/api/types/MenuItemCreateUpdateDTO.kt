package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length

class MenuItemCreateUpdateDTO(
    @field:Schema(description = "ID for the event")
    val id: Long?,

    @field:Schema(description = "Event Id, must be provided")
    @field:NotNull(message = "Please provide an Event Id") // Ensure the eventId is not null
    @field:Min(value = 1, message = "Provide a valid Event Id") // Ensure eventId is greater than 0
    val eventId: Long?,

    @field:Schema(description = "Name for the Menu Item")
    @field:NotBlank(message = "Menu item name must be provided")
    @field:NotEmpty(message = "Menu item name must be provided")
    @field:Length(min = 1, max = 64, message = "Menu item name must be between 1 and 64 chars long")
    val name: String?,
) {}