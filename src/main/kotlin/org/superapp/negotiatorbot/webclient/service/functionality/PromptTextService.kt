package org.superapp.negotiatorbot.webclient.service.functionality

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import org.superapp.negotiatorbot.webclient.enum.LegalType
import org.superapp.negotiatorbot.webclient.enum.PromptType
import org.superapp.negotiatorbot.webclient.repository.PromptTextRepository

interface PromptTextService {
    fun fetchPrompt(legalType: LegalType, documentType: DocumentType, promptType: PromptType): String
}

@Service
class PromptTextServiceImpl(private val promptTextRepository: PromptTextRepository) : PromptTextService {
    override fun fetchPrompt(legalType: LegalType, documentType: DocumentType, promptType: PromptType): String {
        return promptTextRepository.findByAuditoryAndTypeAndPromptType(legalType, documentType, promptType)
            .orElseThrow { NoSuchElementException() }.promptText!!
    }

}