package org.superapp.negotiatorbot.webclient.dto

data class TaskRecordDto(
    val id: Long,
    val relatedId: Long,
    val status: TaskDtoStatus,
    val result: String?
)

enum class TaskDtoStatus {
    CREATED, IN_PROGRESS, FAILED, FINISHED
}
