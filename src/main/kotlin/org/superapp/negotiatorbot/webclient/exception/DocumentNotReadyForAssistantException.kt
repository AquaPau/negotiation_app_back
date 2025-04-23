package org.superapp.negotiatorbot.webclient.exception

import org.springframework.http.HttpStatus

class DocumentNotReadyForAssistantException : CustomUiException(
    HttpStatus.NOT_ACCEPTABLE,
    "Документ не может быть обработан: не заданы все необходимые параметры"
)