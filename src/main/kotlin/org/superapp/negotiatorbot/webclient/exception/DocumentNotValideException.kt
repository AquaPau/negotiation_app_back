package org.superapp.negotiatorbot.webclient.exception

import org.springframework.http.HttpStatus

class DocumentNotValidException(customMessage: String) : CustomUiException(HttpStatus.NOT_ACCEPTABLE, customMessage)