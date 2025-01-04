package org.superapp.negotiatorbot.webclient.dto.result

data class CompanyOpportunitiesDto(
    val id: Int,
    val name: String,
    val opportunities: List<OpportunityDto> = emptyList()
)

data class OpportunityDto(
    val description: String
)