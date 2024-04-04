package ch.zhaw.pm4.simonsays.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule
import org.springframework.context.annotation.Configuration

@Configuration
class JsonOrgModuleConfig(
    objectMapper: ObjectMapper
) {
    init {
        objectMapper.registerModule(JsonOrgModule())
    }
}