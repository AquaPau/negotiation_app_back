package org.superapp.negotiatorbot.botclient.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service

@Service
class QueryMappingService() {
    private val MAX_BYTE_LENGTH = 64;

    val objectMapper = jacksonObjectMapper()
    val divider = '~'
    fun toCallbackQuery(mappingQuery: String, payload: Any): String {
        val query = "$mappingQuery$divider" + objectMapper.writeValueAsString(payload)
        validate(query)
        return query
    }

    fun toMapQuery(fullCallbackQuery: String): String {
        return fullCallbackQuery.substringBefore(divider)
    }

    fun <T> getPayload(fullCallbackQuery: String, payloadClass: Class<T>): T =
        objectMapper.readValue(fullCallbackQuery.getPayload(), payloadClass)

    private fun validate(fullCallbackQuery: String) {
        val byteSize = fullCallbackQuery.toByteArray(Charsets.UTF_8).size
        if (byteSize > MAX_BYTE_LENGTH) {
            throw IllegalArgumentException("Invalid byte length. query: [$fullCallbackQuery] length: [$byteSize] max_allowed: [$MAX_BYTE_LENGTH]")
        }
    }

    private fun String.getPayload(): String = this.substringAfter(divider)
}