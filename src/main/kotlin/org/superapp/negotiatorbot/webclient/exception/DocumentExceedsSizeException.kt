package org.superapp.negotiatorbot.webclient.exception

import org.springframework.http.HttpStatus

class DocumentExceedsSizeException : CustomUiException(
    HttpStatus.NOT_ACCEPTABLE,
    "Превышен предельный разрешенный размер документа"
) {

}