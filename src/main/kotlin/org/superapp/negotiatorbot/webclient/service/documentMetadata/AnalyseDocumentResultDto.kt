package org.superapp.negotiatorbot.webclient.service.documentMetadata

import org.superapp.negotiatorbot.webclient.enums.PromptType

data class AnalyseDocumentResultDto(
    val id: Long,
    val documentId: Long,
    val promptType: PromptType,
    val result: String
)
