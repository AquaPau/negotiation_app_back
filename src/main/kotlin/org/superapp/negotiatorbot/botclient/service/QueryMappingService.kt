package org.superapp.negotiatorbot.botclient.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class QueryMappingService(private val objectMapper: ObjectMapper) {
    val divider = "~"
    fun toCallbackQuery(mappingQuery: String, payload: Any): String {
        return "$mappingQuery$divider" +objectMapper.writeValueAsString(payload).removeSurrounding("\"")
    }

    fun toMapQuery(fullCallbackQuery: String): String {
        val dividerIndex = fullCallbackQuery.indexOf(divider)
        return fullCallbackQuery.substring(dividerIndex+1)
    }

}