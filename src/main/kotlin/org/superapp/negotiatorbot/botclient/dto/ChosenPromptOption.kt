package org.superapp.negotiatorbot.botclient.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.superapp.negotiatorbot.webclient.enums.PromptType

/**
 * Keep this with short names. Can handle less then 50 bytes when serialized to json
 */
data class ChosenPromptOption(
    @JsonProperty("1")
    val tgDocumentDbId: Long,
    @JsonProperty("2")
    val promptType: PromptType
)
