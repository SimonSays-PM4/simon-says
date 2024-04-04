package ch.zhaw.pm4.simonsays.api.types

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.Length

data class EventCreateUpdateDTO (

        @field:Schema(description = "ID for the event")
        val id: Long?,

        @field:Schema(description = "Name for the event")
        @field:NotBlank(message = "Event name must be provided")
        @field:NotEmpty(message = "Event name must be provided")
        @field:Length(min = 5, max = 64, message = "Event name must be between 5 and 64 chars long")
        val name: String?,

        @field:Schema(description = "Event description")
        @field:NotBlank(message = "Event password must be provided")
        @field:NotEmpty(message = "Event password must be provided")
        @field:Length(min = 8, max = 64, message = "Event password must be between 8 and 64 chars long")
        val password: String?,

        @field:Schema(description = "Number of tables available at the event")
        @field:Min(value = 0, message = "Number of tables must be greater or equal to 0")
        val numberOfTables: Long?
)
