package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

class MenuCreateUpdateDTO (
    @field:Schema(description = "ID for the menu")
    val id: Long?,

    @field:Schema(description = "Name for the Menu")
    @field:NotBlank(message = "Menu name must be provided")
    @field:NotEmpty(message = "Menu name must be provided")
    @field:Length(min = 1, max = 64, message = "Menu name must be between 1 and 64 chars long")
    val name: String?,

    @field:NotNull(message = "Menu item list must be provided")
    @field:Size(min = 1, message = "A menu must have at least one menu item")
    val menuItems: List<MenuItemDTO>?,
)