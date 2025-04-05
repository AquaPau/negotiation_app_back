package org.superapp.negotiatorbot.webclient.exception

import org.springframework.http.HttpStatus

sealed class CustomUiException(val httpStatus: HttpStatus, override val message: String): RuntimeException(message)