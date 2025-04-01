package org.superapp.negotiatorbot.botclient.dto

import org.superapp.negotiatorbot.webclient.enums.DocumentType

data class ChosenDocumentOption(
    val tgDocumentId: Long,
    val documentType: DocumentType,
)