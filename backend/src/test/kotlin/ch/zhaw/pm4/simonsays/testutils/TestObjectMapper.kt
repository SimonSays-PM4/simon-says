package ch.zhaw.pm4.simonsays.testutils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule

fun testObjectMapper(): ObjectMapper {
    val objectMapper = jacksonObjectMapper()
    objectMapper.registerModule(JsonOrgModule())
    return objectMapper
}