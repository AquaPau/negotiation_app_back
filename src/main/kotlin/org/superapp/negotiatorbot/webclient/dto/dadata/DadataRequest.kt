package org.superapp.negotiatorbot.webclient.dto.dadata

data class DadataRequest(
    val query: String,
    val count: Int = 1
)