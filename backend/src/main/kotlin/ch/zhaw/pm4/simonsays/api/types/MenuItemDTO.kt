package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema

data class MenuItemDTO (
        @field:Schema(description = "ID of the menu item")
        val id: Long,
        @field:Schema(description = "Name for the menu item")
        val name: String,
        @field:Schema(description = "List of ingredients")
        var ingredients: List<IngredientDTO>,
        @field:Schema(description = "Price of the menu item")
        var price: Long
)