package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema

data class MenuItemDTO (
        @field:Schema(description = "ID of the event")
        val id: Long?,
        @field:Schema(description = "Name for the menu item")
        val name: String,
)