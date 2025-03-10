package org.superapp.negotiatorbot.webclient.dto.project

import org.superapp.negotiatorbot.webclient.enum.CompanyRegion

data class ProjectDto(
    val customUserGeneratedName: String,
    val userId: Long,
    val userGeneratedPrompt: String
)
