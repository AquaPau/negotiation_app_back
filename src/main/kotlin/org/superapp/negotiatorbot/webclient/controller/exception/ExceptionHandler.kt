package org.superapp.negotiatorbot.webclient.controller.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.superapp.negotiatorbot.webclient.exception.CustomUiException

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(CustomUiException::class)
    @ResponseBody
    fun commonNotFoundException(exception: CustomUiException) =
        ResponseEntity(exception.message, exception.httpStatus)
}