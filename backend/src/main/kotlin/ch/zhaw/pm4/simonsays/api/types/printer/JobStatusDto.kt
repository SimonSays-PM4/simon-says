package ch.zhaw.pm4.simonsays.api.types.printer

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class JobStatusDto {
    PENDING, PRINTED, ERROR, CANCELED;

    @JsonValue
    fun toJson(): String {
        return name.lowercase()
    }

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromJson(value: String): JobStatusDto {
            return JobStatusDto.valueOf(value.uppercase())
        }
    }
}
