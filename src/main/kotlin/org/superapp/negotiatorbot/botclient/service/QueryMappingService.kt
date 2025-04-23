package org.superapp.negotiatorbot.botclient.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service

@Service
class QueryMappingService {

    fun toCallbackQuery(mappingQuery: String, payload: Any): String {
        val query = "$mappingQuery$DIVIDER" + objectMapper.writeValueAsString(payload)
        validate(query)
        return query
    }

    fun toMapQuery(fullCallbackQuery: String): String {
        return fullCallbackQuery.substringBefore(DIVIDER)
    }

    fun <T> getPayload(fullCallbackQuery: String, payloadClass: Class<T>): T =
        objectMapper.readValue(fullCallbackQuery.getPayload(), payloadClass)

    private fun validate(fullCallbackQuery: String) {
        val byteSize = fullCallbackQuery.toByteArray(Charsets.UTF_8).size
        if (byteSize > MAX_BYTE_LENGTH) {
            throw IllegalArgumentException(
                "Invalid byte length. query: [$fullCallbackQuery] length: [$byteSize] max_allowed: [${MAX_BYTE_LENGTH}]"
            )
        }
    }

    private fun String.getPayload(): String = this.substringAfter(DIVIDER)

    companion object {
        private const val MAX_BYTE_LENGTH = 64
        private val objectMapper = jacksonObjectMapper()
        private const val DIVIDER = '~'
    }
}