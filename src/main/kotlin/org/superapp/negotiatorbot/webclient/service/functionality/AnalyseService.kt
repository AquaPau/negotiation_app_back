package org.superapp.negotiatorbot.webclient.service.functionality

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.LegalType
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.superapp.negotiatorbot.webclient.exception.PromptNotFoundException
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService
import org.superapp.negotiatorbot.webclient.service.functionality.openai.OpenAiUserService
import org.superapp.negotiatorbot.webclient.service.task.OpenAiTaskService


interface AnalyseService {
    fun detectRisks(documentId: Long, legalType: LegalType)

    fun provideDescription(documentId: Long, legalType: LegalType)

    fun provideProjectResolution(projectId: Long)

    fun updateThreadAndRun(documentId: Long, consumer: (Long) -> Unit)
}

@Service
class AnalyseServiceImpl(
    private val documentService: DocumentService,
    private val openAiUserService: OpenAiUserService,
    private val promptTextService: PromptTextService,
    private val openAiTaskService: OpenAiTaskService
) : AnalyseService {

    override fun detectRisks(documentId: Long, legalType: LegalType) {
        val doc = documentService.get(documentId)
        val introPrompt = promptTextService.fetchPrompt(legalType, DocumentType.DEFAULT, PromptType.DEFAULT)
        val prompt = try {
            promptTextService.fetchPrompt(legalType, doc.documentType!!, PromptType.RISKS)
        } catch (e: PromptNotFoundException) {
            promptTextService.fetchPrompt(legalType, DocumentType.DEFAULT, PromptType.RISKS)
        }
        openAiTaskService.execute(doc, "$introPrompt $prompt", PromptType.RISKS)
    }

    override fun provideDescription(documentId: Long, legalType: LegalType) {
        val doc = documentService.get(documentId)
        val introPrompt = promptTextService.fetchPrompt(legalType, DocumentType.DEFAULT, PromptType.DEFAULT)
        val prompt = try {
            promptTextService.fetchPrompt(legalType, doc.documentType!!, PromptType.DESCRIPTION)
        } catch (e: PromptNotFoundException) {
            promptTextService.fetchPrompt(legalType, DocumentType.DEFAULT, PromptType.DESCRIPTION)
        }
        openAiTaskService.execute(doc, "$introPrompt $prompt", PromptType.DESCRIPTION)

    }

    override fun provideProjectResolution(projectId: Long) {

    }

    override fun updateThreadAndRun(documentId: Long, consumer: (Long) -> Unit) {
        val document = documentService.get(documentId)
        openAiUserService.updateThread(document)
        consumer.invoke(documentId)
    }
}