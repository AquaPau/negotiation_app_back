package org.superapp.negotiatorbot.webclient.entity.task

enum class TaskStatus {
    CREATED,
    DOWNLOADED_FROM_S3,
    ERROR_DOWNLOAD_S3,
    SENT_TO_ASSISTANT,
    ERROR_UPLOADING_TO_ASSISTANT,
    ERROR_ASSISTANT_REPLY,
    SUCCESS_ASSISTANT_REPLY,
}