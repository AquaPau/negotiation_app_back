package org.superapp.negotiatorbot.webclient.enums

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