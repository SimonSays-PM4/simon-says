package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema

data class EventDTO (
        @field:Schema(description = "Name for the event")
        val name: String,
        @field:Schema(description = "Number of tables available at the event")
        val numberOfTables: Long,
        @field:Schema(description = "ID of the event")
        val id: Long?
)