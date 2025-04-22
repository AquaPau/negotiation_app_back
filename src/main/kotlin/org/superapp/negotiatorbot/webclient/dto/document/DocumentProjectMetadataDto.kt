package org.superapp.negotiatorbot.webclient.dto.document

import org.superapp.negotiatorbot.webclient.enums.DocumentType

class DocumentProjectMetadataDto(
    val id: Long,
    val userId: Long,
    var projectId: Long? = null,
    val name: String,
    val type: DocumentType
)