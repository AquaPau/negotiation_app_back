package org.superapp.negotiatorbot.webclient.dto.project

import org.superapp.negotiatorbot.webclient.enums.TaskStatus

data class ProjectDto(
    val id: Long,
    val customUserGeneratedName: String,
    val userId: Long,
    val userGeneratedPrompt: String,
    val taskHistory: List<ProjectTaskHistory>,
    val taskResult: String?
)

data class ProjectTaskHistory(
    val id: Long,
    val status: TaskStatus
)