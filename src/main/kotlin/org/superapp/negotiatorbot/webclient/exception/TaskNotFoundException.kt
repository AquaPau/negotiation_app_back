package org.superapp.negotiatorbot.webclient.exception

import org.springframework.http.HttpStatus

class TaskNotFoundException(taskId: Long?, relatedId: Long? = null) :
    CustomUiException(
        HttpStatus.NOT_FOUND,
        if (relatedId != null) "Задача к Open AI для id $relatedId не найдена" else "Задача к Open AI с номером $taskId не найдена"
    )