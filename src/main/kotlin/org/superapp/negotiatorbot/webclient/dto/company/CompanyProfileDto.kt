package org.superapp.negotiatorbot.webclient.dto.company

data class CompanyProfileDto(
    val id: Long,
    val customUserGeneratedName: String,
    val userId: Long,
    val inn: String? = null,
    val ogrn: String? = null,
    val fullName: String? = null,
    val managerName: String? = null,
    val managerTitle: String? = null,
    val documents: List<CompanyDocumentDescriptionDto>
)