package org.superapp.negotiatorbot.webclient.dto.company

data class CompanyDocumentDescriptionDto(
    val id: Long,
    val customUserGeneratedName: String,
    val description: String? = null
)
