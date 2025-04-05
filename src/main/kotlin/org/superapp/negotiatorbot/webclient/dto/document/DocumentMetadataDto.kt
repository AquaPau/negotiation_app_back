package org.superapp.negotiatorbot.webclient.dto.document

import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.TaskStatus

data class DocumentMetadataDto(
    val id: Long,
    val userId: Long,
    var companyId: Long? = null,
    var counterPartyId: Long? = null,
    val name: String,
    val type: DocumentType,
    val description: DescriptionData?,
    val risks: RisksData?
)

data class DescriptionData(
    val text: String?,
    val status: TaskStatus,
    val id: Long
)

data class RisksData(
    val text: String?,
    val status: TaskStatus,
    val id: Long
)