package org.superapp.negotiatorbot.webclient.exception

class CompanyAlreadyExistsException(orgn: Long): RuntimeException("Company with ogrn $orgn already exists")