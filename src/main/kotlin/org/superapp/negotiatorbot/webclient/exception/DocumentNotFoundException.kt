package org.superapp.negotiatorbot.webclient.exception

import org.springframework.http.HttpStatus

class DocumentNotFoundException(id: Long): CustomUiException(HttpStatus.NOT_FOUND, "Документ с id $id не найден") {
}