package org.superapp.negotiatorbot.webclient.dto.result

data class CompanyRisksDto(
    val id: Int,
    val name: String,
    val risks: List<RiskDto> = emptyList()
)

data class RiskDto(
    val description: String
)
