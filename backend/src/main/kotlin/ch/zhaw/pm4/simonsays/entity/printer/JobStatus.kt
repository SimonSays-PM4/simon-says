package ch.zhaw.pm4.simonsays.entity.printer

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class JobStatus {
    PENDING, PRINTED, ERROR, CANCELED;
}
