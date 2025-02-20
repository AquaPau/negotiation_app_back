package org.superapp.negotiatorbot.webclient.dto.document

import org.superapp.negotiatorbot.webclient.enum.DocumentType

data class DocumentMetadataDto(
    val id: Long,
    val userId: Long,
    val companyId: Long?,
    val counterPartyId: Long?,
    val name: String,
    val type: DocumentType,
    val description: String?,
    val risks: String?
)