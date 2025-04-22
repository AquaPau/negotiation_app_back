package org.superapp.negotiatorbot.webclient.dto.document

import org.superapp.negotiatorbot.webclient.dto.TaskDtoStatus
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.TaskStatus

data class DocumentEnterpriseMetadataDto(
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
    val status: TaskDtoStatus,
    val id: Long
)

data class RisksData(
    val text: String?,
    val status: TaskDtoStatus,
    val id: Long
)