package org.superapp.negotiatorbot.webclient.service.functionality.openai

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
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

@Service
class WebOpenAiService(
    openAiAssistantService: OpenAiAssistantService,
    taskService: TaskRecordService,
    private val s3Service: S3Service,
    private val companyAssistantService: AssistantService<UserCompany>,
    private val contractorAssistantService: AssistantService<UserContractor>,
    private val projectAssistantService: AssistantService<Project>,
) : AbstractOpenAiService<DocumentMetadata>(openAiAssistantService, taskService) {

    override fun createAssistant(
        doc: DocumentMetadata,
        taskRecord: TaskRecord
    ): OpenAiAssistant {
        val fileContent = s3Service.download(doc, taskRecord)
        val openAiAssistant = getAssistantByDocument(doc)
        val fullDocName = doc.getNameWithExtension()
        uploadFile(openAiAssistant, fileContent.inputStream, fullDocName, taskRecord)
        return openAiAssistant
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun provideResponseFromOpenAi(
        taskRecord: TaskRecord,
        doc: List<DocumentMetadata>,
        prompt: String
    ): String {
        val assistants = doc.map { getAssistantByDocument(it) }.distinct()
            .ifEmpty { throw TaskException(TaskStatus.ERROR_UPLOADING_TO_ASSISTANT) }
        if (assistants.size > 1) throw TaskException(TaskStatus.ERROR_UPLOADING_TO_ASSISTANT)
        val assistant = assistants.first()
        doc.forEach {
            uploadFile(assistant, s3Service.download(it, taskRecord).inputStream, it.getNameWithExtension(), taskRecord)
        }
        val result = startDialogWIthUserPrompt(assistant, prompt)
        deleteFilesFromOpenAi(assistant)
        return formatTheResultWithoutMarkdown(result)
    }

    fun updateThread(documentMetadata: DocumentMetadata) {
        val openAiAssistant = getAssistantByDocument(documentMetadata)
        openAiAssistantService.updateThread(openAiAssistant)
    }

    fun getAssistantByDocument(documentMetadata: DocumentMetadata): OpenAiAssistant {
        val relatedId = documentMetadata.relatedId!!
        return when (documentMetadata.businessType) {
            BusinessType.USER -> companyAssistantService.getAssistant(relatedId)
            BusinessType.PARTNER -> contractorAssistantService.getAssistant(relatedId)
            BusinessType.PROJECT -> projectAssistantService.getAssistant(relatedId)
            else -> throw UnsupportedOperationException()
        }
    }
}