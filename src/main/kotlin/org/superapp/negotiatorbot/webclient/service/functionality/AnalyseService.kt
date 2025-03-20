package org.superapp.negotiatorbot.webclient.service.functionality

import kotlinx.coroutines.DelicateCoroutinesApi
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.LegalType
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService
import org.superapp.negotiatorbot.webclient.service.functionality.openai.OpenAiUserService
import org.superapp.negotiatorbot.webclient.service.task.OpenAiTaskService


interface AnalyseService {
    fun detectRisks(documentId: Long, legalType: LegalType)

    fun provideDescription(documentId: Long, legalType: LegalType)

    fun analyseCase(projectId: Long)

    fun updateThreadAndRun(documentId: Long, consumer: (Long) -> Unit)
}

@Service
class AnalyseServiceImpl(
    private val documentService: DocumentService,
    private val openAiUserService: OpenAiUserService,
    private val promptTextService: PromptTextService,
    private val openAiTaskService: OpenAiTaskService
) : AnalyseService {

    @OptIn(DelicateCoroutinesApi::class)
    override fun detectRisks(documentId: Long, legalType: LegalType) {
        val doc = documentService.get(documentId)
        val introPrompt = promptTextService.fetchPrompt(legalType, DocumentType.DEFAULT, PromptType.DEFAULT)
        val prompt = try {
            promptTextService.fetchPrompt(legalType, doc.documentType!!, PromptType.RISKS)
        } catch (e: NoSuchElementException) {
            promptTextService.fetchPrompt(legalType, DocumentType.DEFAULT, PromptType.RISKS)
        }
        doc.risks = "Риски документа загружаются"
        documentService.save(doc)
        openAiTaskService.execute(doc, "$introPrompt prompt", PromptType.RISKS)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun provideDescription(documentId: Long, legalType: LegalType) {
        val doc = documentService.get(documentId)
        val introPrompt = promptTextService.fetchPrompt(legalType, DocumentType.DEFAULT, PromptType.DEFAULT)
        val prompt = try {
            promptTextService.fetchPrompt(legalType, doc.documentType!!, PromptType.DESCRIPTION)
        } catch (e: NoSuchElementException) {
            promptTextService.fetchPrompt(legalType, DocumentType.DEFAULT, PromptType.DESCRIPTION)
        }
        doc.description = "Содержимое документа загружается"
        documentService.save(doc)
        openAiTaskService.execute(doc, "$introPrompt $prompt", PromptType.DESCRIPTION)

    }

    override fun analyseCase(projectId: Long) {

    }

    override fun updateThreadAndRun(documentId: Long, consumer: (Long) -> Unit) {
        val document = documentService.get(documentId)
        openAiUserService.updateThread(document)
        consumer.invoke(documentId)
    }
}