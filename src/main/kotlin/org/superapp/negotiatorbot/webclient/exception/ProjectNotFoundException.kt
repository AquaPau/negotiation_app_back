package org.superapp.negotiatorbot.webclient.exception

import org.springframework.http.HttpStatus

class ProjectNotFoundException(id: Long?, relatedId: Long? = null) : CustomUiException(
    HttpStatus.NOT_FOUND,
    if (relatedId != null) "Проект для $relatedId не найден" else "Проект с id $id не найден"
)