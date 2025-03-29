package org.superapp.negotiatorbot.webclient.exception

import org.springframework.http.HttpStatus

class CompanyAlreadyExistsException(orgn: Long) :
    CustomUiException(HttpStatus.BAD_REQUEST, "Компания с ОГРН $orgn уже существует")