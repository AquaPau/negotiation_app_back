package org.superapp.negotiatorbot.webclient.exception

import org.springframework.http.HttpStatus

class DocumentFormatIsNotAppropriateException : CustomUiException(
    HttpStatus.NOT_ACCEPTABLE,
    "Формат загружаемого документа невалиден. Пожалуйста, выберите другой документ и попробуйте снова"
) {
}