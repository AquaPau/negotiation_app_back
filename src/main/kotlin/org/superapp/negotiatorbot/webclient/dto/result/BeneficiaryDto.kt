package org.superapp.negotiatorbot.webclient.dto.result

data class BeneficiaryDto(
    val fullName: String,
    val id: String,
    val residence: String,
    val participationPercentage: Int
)
