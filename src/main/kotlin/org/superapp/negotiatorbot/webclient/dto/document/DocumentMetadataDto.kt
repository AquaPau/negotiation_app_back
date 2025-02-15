package org.superapp.negotiatorbot.webclient.dto.document

data class DocumentMetadataDto(
    val id: Long,
    val userId: Long,
    val counterPartyId: Long?,
    val name: String,
    val description: String?
)