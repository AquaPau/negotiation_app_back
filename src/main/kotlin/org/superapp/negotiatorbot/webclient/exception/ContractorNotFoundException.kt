package org.superapp.negotiatorbot.webclient.exception

import org.springframework.http.HttpStatus

class ContractorNotFoundException(id: Long?, message: String? = null) :
    CustomUiException(HttpStatus.NOT_FOUND, message ?: "Контрагент с id $id не найден") {
}