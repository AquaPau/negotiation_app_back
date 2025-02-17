package org.superapp.negotiatorbot.webclient.dto.company

import org.superapp.negotiatorbot.webclient.enum.CompanyRegion

data class CompanyProfileDto(
    val id: Long,
    val customUserGeneratedName: String,
    val residence: CompanyRegion,
    val userId: Long,
    val inn: String? = null,
    val ogrn: String? = null,
    val fullName: String? = null,
    val managerName: String? = null,
    val managerTitle: String? = null
)