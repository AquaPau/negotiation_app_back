package org.superapp.negotiatorbot.webclient.dto

import org.superapp.negotiatorbot.webclient.enums.TaskStatus

data class TaskRecordDto(
    val id: Long,
    val relatedId: Long,
    val status: TaskStatus,
    val result: String?
)
