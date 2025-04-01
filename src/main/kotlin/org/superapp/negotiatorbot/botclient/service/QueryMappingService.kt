package org.superapp.negotiatorbot.botclient.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service

@Service
class QueryMappingService() {
    val objectMapper = jacksonObjectMapper()
    val divider = '~'
    fun toCallbackQuery(mappingQuery: String, payload: Any): String {
        return "$mappingQuery$divider" + objectMapper.writeValueAsString(payload)
    }

    fun toMapQuery(fullCallbackQuery: String): String {
        return fullCallbackQuery.substringBefore(divider)
    }

    fun <T> getPayload(fullCallbackQuery: String, payloadClass: Class<T>): T =
        objectMapper.readValue(fullCallbackQuery.getPayload(), payloadClass)

    private fun String.getPayload(): String = this.substringAfter(divider)
}