package org.superapp.negotiatorbot.webclient.exception

import org.springframework.http.HttpStatus
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.LegalType
import org.superapp.negotiatorbot.webclient.enums.PromptType

class PromptNotFoundException(legalType: LegalType, documentType: DocumentType?, promptType: PromptType) :
    CustomUiException(
        HttpStatus.NOT_FOUND,
        "Промпт для сочетания типов $legalType ${documentType ?: "all documents"} $promptType не найден"
    )