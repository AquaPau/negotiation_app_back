package org.superapp.negotiatorbot.webclient.dto.project

import org.superapp.negotiatorbot.webclient.dto.TaskDtoStatus

data class ProjectDto(
    val id: Long,
    val customUserGeneratedName: String,
    val userId: Long,
    val userGeneratedPrompt: String,
    val resolution: Resolution?
)

data class Resolution(
    val text: String?,
    val status: TaskDtoStatus,
    val id: Long
)