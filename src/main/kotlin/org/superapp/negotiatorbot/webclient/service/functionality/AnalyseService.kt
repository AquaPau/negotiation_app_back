package org.superapp.negotiatorbot.webclient.service.functionality

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.LegalType
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.superapp.negotiatorbot.webclient.exception.PromptNotFoundException
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService
import org.superapp.negotiatorbot.webclient.service.functionality.openai.WebOpenAiService
import org.superapp.negotiatorbot.webclient.service.project.ProjectService
import org.superapp.negotiatorbot.webclient.service.task.OpenAiTaskService
import org.superapp.negotiatorbot.webclient.service.user.UserService


interface AnalyseService {
    fun detectRisks(documentId: Long, legalType: LegalType)

    fun provideDescription(documentId: Long, legalType: LegalType)

    fun provideProjectResolution(projectId: Long)

    fun updateThreadAndRun(documentId: Long, isProject: Boolean = false, consumer: (Long) -> Unit)
}

@Service
class AnalyseServiceImpl(
    private val documentService: DocumentService,
    private val webOpenAiService: WebOpenAiService,
    private val promptTextService: PromptTextService,
    private val openAiTaskService: OpenAiTaskService,
    private val userService: UserService,
    private val projectService: ProjectService
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
        val currentUser = userService.getCurrentUser()
        val project = projectService.getProjectById(projectId)
        val documents = documentService.getDocumentList(projectId, currentUser.id!!, BusinessType.PROJECT)
        val introPrompt = promptTextService.fetchPrompt(LegalType.ENTERPRISE, PromptType.PROJECT)
        val prompt = "$introPrompt ${project.userGeneratedPrompt}"
        openAiTaskService.execute(project, prompt, PromptType.PROJECT, documents)

    }

    override fun updateThreadAndRun(relatedId: Long, isProject: Boolean, consumer: (Long) -> Unit) {
        if (isProject) {
            val currentUser = userService.getCurrentUser()
            val project = projectService.getProjectById(relatedId)
            documentService.getDocumentList(
                project.id!!,
                currentUser.id!!,
                BusinessType.PROJECT
            ).forEach { webOpenAiService.updateThread(it) }
        } else {
            val document = documentService.get(relatedId)
            webOpenAiService.updateThread(document)
        }
        consumer.invoke(relatedId)
    }
}