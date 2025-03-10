package org.superapp.negotiatorbot.webclient.dto.project

data class NewProjectDto(
    val customUserGeneratedName: String,
    val userId: Long,
    val userGeneratedPrompt: String
)
