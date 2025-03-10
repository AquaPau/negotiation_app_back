package org.superapp.negotiatorbot.webclient.service.functionality

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.entity.UserCompany
import org.superapp.negotiatorbot.webclient.entity.UserContractor
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.enum.BusinessType
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import org.superapp.negotiatorbot.webclient.enum.LegalType
import org.superapp.negotiatorbot.webclient.enum.PromptType
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService
import org.superapp.negotiatorbot.webclient.service.functionality.openai.OpenAiUserService
import org.superapp.negotiatorbot.webclient.service.project.ProjectService
import org.superapp.negotiatorbot.webclient.service.s3.S3Service
import org.superapp.negotiatorbot.webclient.service.user.UserService


interface AnalyseService {
    fun detectRisks(documentId: Long, legalType: LegalType)

    fun provideDescription(documentId: Long, legalType: LegalType)

    fun analyseCase(projectId: Long)

    fun updateThreadAndRun(documentId: Long, consumer: (Long) -> Unit)
}

@Service
class AnalyseServiceImpl(
    private val documentService: DocumentService,
    private val projectService: ProjectService,
    private val userService: UserService,
    private val companyAssistantService: AssistantService<UserCompany>,
    private val contractorAssistantService: AssistantService<UserContractor>,
    private val projectAssistantService: AssistantService<Project>,
    private val openAiUserService: OpenAiUserService,
    private val promptTextService: PromptTextService,
    private val s3Service: S3Service
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
        GlobalScope.launch {
            doc.risks = provideResponseFromOpenAi(doc, "$introPrompt. $prompt")
            documentService.save(doc)
        }
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
        GlobalScope.launch {
            doc.description = provideResponseFromOpenAi(doc, "$introPrompt. $prompt")
            documentService.save(doc)
        }

    }

    override fun analyseCase(projectId: Long) {

    }

    override fun updateThreadAndRun(documentId: Long, consumer: (Long) -> Unit) {
        val assistant = documentService.get(documentId).getAssistant()
        openAiUserService.updateThread(assistant)
        consumer.invoke(documentId)
    }

    private suspend fun provideResponseFromOpenAi(doc: DocumentMetadata, prompt: String): String {
        val fileContent = s3Service.download(doc.path!!)
        val openAiAssistant = doc.getAssistant()
        val fullDocName = doc.getNameWithExtension()

        openAiUserService.uploadFile(openAiAssistant, fileContent.inputStream, fullDocName)
        val result = openAiUserService.startDialogWIthUserPrompt(openAiAssistant, prompt)
        openAiUserService.deleteFilesFromOpenAi(openAiAssistant)
        return result.replace(
            regex = Regex("""【.*】"""),
            ""
        )
    }

    private fun DocumentMetadata.getAssistant(): OpenAiAssistant {
        val relatedId = this.relatedId!!
        return when (this.businessType) {
            BusinessType.USER -> companyAssistantService.getAssistant(relatedId)
            BusinessType.PARTNER -> contractorAssistantService.getAssistant(relatedId)
            BusinessType.PROJECT -> projectAssistantService.getAssistant(relatedId)
            else -> throw UnsupportedOperationException()
        }
    }
}