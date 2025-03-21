package org.superapp.negotiatorbot.webclient.dto.company

import org.superapp.negotiatorbot.webclient.enums.CompanyRegion
import org.superapp.negotiatorbot.webclient.enums.CompanyRegion.*

data class NewCompanyProfile(
    val customUserGeneratedName: String,
    val region: CompanyRegion = RU,
    val userId: Long,
    val ogrn: Long
)