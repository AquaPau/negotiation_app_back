package org.superapp.negotiatorbot.webclient.dto.project

data class ProjectDto(
    val customUserGeneratedName: String,
    val userId: Long,
    val userGeneratedPrompt: String
)
