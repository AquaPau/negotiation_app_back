package org.superapp.negotiatorbot.webclient.exception

import org.springframework.http.HttpStatus

class CompanyNotFoundException(id: Long?, message: String? = null) :
    CustomUiException(HttpStatus.NOT_FOUND, message ?: "Компания с id $id не найдена")