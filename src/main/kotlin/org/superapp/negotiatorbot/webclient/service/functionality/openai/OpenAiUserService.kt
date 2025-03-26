package org.superapp.negotiatorbot.webclient.service.functionality.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.content.*
import jakarta.transaction.Transactional
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.entity.UserCompany
import org.superapp.negotiatorbot.webclient.entity.UserContractor
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.exception.TaskException
import org.superapp.negotiatorbot.webclient.service.functionality.AssistantService
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskRecordService
import org.superapp.negotiatorbot.webclient.service.s3.S3Service
import java.io.InputStream

private val log = KotlinLogging.logger {}

/**
 * User logic umbrella functions for access Open API capabilities
 */

interface OpenAiUserService {

    fun updateThread(documentMetadata: DocumentMetadata)

    fun provideResponseFromOpenAi(taskRecord: TaskRecord, doc: DocumentMetadata, prompt: String): String

    fun provideResponseFromOpenAi(taskRecord: TaskRecord, doc: List<DocumentMetadata>, prompt: String): String

    fun getAssistantByDocument(documentMetadata: DocumentMetadata): OpenAiAssistant
}

@Service
@Transactional
class OpenAiUserServiceImpl(
    private val openAiAssistantService: OpenAiAssistantService,
    private val taskService: TaskRecordService,
    private val s3Service: S3Service,
    private val companyAssistantService: AssistantService<UserCompany>,
    private val contractorAssistantService: AssistantService<UserContractor>,
    private val projectAssistantService: AssistantService<Project>,
) : OpenAiUserService {

    override fun provideResponseFromOpenAi(taskRecord: TaskRecord, doc: DocumentMetadata, prompt: String): String {
        val fileContent = s3Service.download(doc, taskRecord)
        val openAiAssistant = getAssistantByDocument(doc)
        val fullDocName = doc.getNameWithExtension()
        uploadFile(openAiAssistant, fileContent.inputStream, fullDocName, taskRecord)

        val result = startDialogWIthUserPrompt(openAiAssistant, prompt, taskRecord)

        deleteFilesFromOpenAi(openAiAssistant)
        return result.replace(
            regex = Regex("""【.*】"""),
            ""
        )
    }

    override fun provideResponseFromOpenAi(
        taskRecord: TaskRecord,
        doc: List<DocumentMetadata>,
        prompt: String
    ): String {
        TODO("Not yet implemented")
    }

    @OptIn(BetaOpenAI::class)
    @Throws(NoSuchElementException::class)
    private fun startDialogWIthUserPrompt(openAiAssistant: OpenAiAssistant, prompt: String, task: TaskRecord): String {
        val response = runBlocking { openAiAssistantService.runRequest(prompt, openAiAssistant) }

        return if (response.isEmpty()) {
            throw TaskException(TaskStatus.ERROR_ASSISTANT_REPLY)
        } else {
            formResponse(response.first())
        }
    }

    private fun uploadFile(
        openAiAssistant: OpenAiAssistant,
        fileContent: InputStream,
        fileName: String,
        task: TaskRecord
    ) {
        try {
            openAiAssistantService.uploadFile(openAiAssistant, fileContent, fileName)
            log.info("Successfully uploaded file [$fileName] to assistant id:[${openAiAssistant.id}]")
            taskService.changeStatus(task, TaskStatus.SENT_TO_ASSISTANT)
        } catch (ignored: Exception) {
            log.error(ignored) { "Failed to upload file [$fileName] to assistant id:[${openAiAssistant.id}]" }
            throw TaskException(TaskStatus.ERROR_UPLOADING_TO_ASSISTANT)
        }
    }


    private fun deleteFilesFromOpenAi(openAiAssistant: OpenAiAssistant) {
        openAiAssistantService.deleteVectorStoreFromAssistant(openAiAssistant)
        log.info("Successfully deleted all filer for $openAiAssistant")
    }

    override fun updateThread(documentMetadata: DocumentMetadata) {
        val openAiAssistant = getAssistantByDocument(documentMetadata)
        openAiAssistantService.updateThread(openAiAssistant)
    }

    override fun getAssistantByDocument(documentMetadata: DocumentMetadata): OpenAiAssistant {
        val relatedId = documentMetadata.relatedId!!
        return when (documentMetadata.businessType) {
            BusinessType.USER -> companyAssistantService.getAssistant(relatedId)
            BusinessType.PARTNER -> contractorAssistantService.getAssistant(relatedId)
            BusinessType.PROJECT -> projectAssistantService.getAssistant(relatedId)
            else -> throw UnsupportedOperationException()
        }
    }

    @OptIn(BetaOpenAI::class)
    private fun formResponse(message: Message): String {
        val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
        return textContent.text.value
    }

}