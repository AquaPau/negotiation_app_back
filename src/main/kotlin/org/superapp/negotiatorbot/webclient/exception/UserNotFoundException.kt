package org.superapp.negotiatorbot.webclient.exception

import org.springframework.http.HttpStatus

class UserNotFoundException(identity: String?) :
    CustomUiException(HttpStatus.NOT_FOUND, "Пользователь с идентификатором $identity не найден")
