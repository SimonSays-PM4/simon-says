package ch.zhaw.pm4.simonsays.api.types

import ch.zhaw.pm4.simonsays.entity.State
import io.swagger.v3.oas.annotations.media.Schema

class OrderIngredientUpdateDTO (
        @field:Schema(description = "Id for the ingredient")
        val id: Long,
        @field:Schema(description = "Name for the ingredient")
        val name: String,
        @field:Schema(description = "the state of the order", enumAsRef = true)
        var state: State,
)