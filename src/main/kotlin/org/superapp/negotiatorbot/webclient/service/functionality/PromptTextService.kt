package org.superapp.negotiatorbot.webclient.service.functionality

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.LegalType
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.superapp.negotiatorbot.webclient.exception.PromptNotFoundException
import org.superapp.negotiatorbot.webclient.repository.PromptTextRepository

interface PromptTextService {
    fun fetchPrompt(legalType: LegalType, documentType: DocumentType, promptType: PromptType): String

    fun fetchPrompt(legalType: LegalType, promptType: PromptType): String
}

@Service
class PromptTextServiceImpl(private val promptTextRepository: PromptTextRepository) : PromptTextService {
    override fun fetchPrompt(legalType: LegalType, documentType: DocumentType, promptType: PromptType): String {
        return promptTextRepository.findByAuditoryAndTypeAndPromptType(legalType, documentType, promptType)
            .orElseThrow { PromptNotFoundException(legalType, documentType, promptType) }.promptText!!
    }

    override fun fetchPrompt(legalType: LegalType, promptType: PromptType): String {
        return promptTextRepository.findByAuditoryAndPromptType(legalType, promptType)
            .orElseThrow { PromptNotFoundException(legalType, null, promptType) }.promptText!!
    }

}