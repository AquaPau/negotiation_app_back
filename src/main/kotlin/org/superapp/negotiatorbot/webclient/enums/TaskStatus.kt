package org.superapp.negotiatorbot.webclient.enums

import org.superapp.negotiatorbot.webclient.dto.TaskDtoStatus
import org.superapp.negotiatorbot.webclient.enums.TaskStatus.*

enum class TaskStatus {
    CREATED,
    DOWNLOADED_FROM_S3,
    ERROR_DOWNLOAD_S3,
    SENT_TO_ASSISTANT,
    ERROR_UPLOADING_TO_ASSISTANT,
    ERROR_ASSISTANT_REPLY,
    FINISHED,
    UNEXPECTED_ERROR,
    ERROR_PARSE_PARAMS
}

fun TaskStatus.toDto(): TaskDtoStatus {
    return when (this) {
        CREATED -> TaskDtoStatus.CREATED
        DOWNLOADED_FROM_S3, SENT_TO_ASSISTANT -> TaskDtoStatus.IN_PROGRESS
        ERROR_DOWNLOAD_S3, ERROR_UPLOADING_TO_ASSISTANT, ERROR_ASSISTANT_REPLY, UNEXPECTED_ERROR, ERROR_PARSE_PARAMS -> TaskDtoStatus.FAILED
        FINISHED -> TaskDtoStatus.FINISHED
    }
}