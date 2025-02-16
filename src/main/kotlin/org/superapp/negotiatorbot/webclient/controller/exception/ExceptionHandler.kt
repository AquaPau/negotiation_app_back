package org.superapp.negotiatorbot.webclient.controller.exception

import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseBody
    fun commonNotFoundException(exception: NoSuchElementException) =
        ResponseEntity("Not found", HttpStatusCode.valueOf(404))
}