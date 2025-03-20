package org.superapp.negotiatorbot.webclient.dto.document

import org.superapp.negotiatorbot.webclient.enums.DocumentType

data class DocumentMetadataDto(
    val id: Long,
    val userId: Long,
    var companyId: Long? = null,
    var counterPartyId: Long? = null,
    val name: String,
    val type: DocumentType,
    val description: String?,
    val risks: String?
)