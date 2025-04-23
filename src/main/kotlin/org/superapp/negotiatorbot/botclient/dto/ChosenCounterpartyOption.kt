package org.superapp.negotiatorbot.botclient.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.superapp.negotiatorbot.botclient.model.TelegramDocumentCounterpartyType

/**
 * Keep this with short names. Can handle less then 50 bytes when serialized to json
 */
data class ChosenCounterpartyOption(
    @JsonProperty("1")
    val tgDocumentDbId: Long,
    @JsonProperty("2")
    val counterparty: TelegramDocumentCounterpartyType
)
