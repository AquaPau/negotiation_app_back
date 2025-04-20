package org.superapp.negotiatorbot.webclient.service.functionality.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.superapp.negotiatorbot.webclient.entity.TaskEnabled
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.exception.TaskException
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskRecordService
import java.io.InputStream

private val log = KotlinLogging.logger {}

/**
 * User logic umbrella functions for access Open API capabilities
 */

@Component
abstract class AbstractOpenAiService<in T : TaskEnabled>(
    protected val openAiAssistantService: OpenAiAssistantService,
    protected val taskService: TaskRecordService,
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun provideResponseFromOpenAi(taskRecord: TaskRecord, task: T, prompt: String): String {
        val openAiAssistant = createAssistant(task, taskRecord)
        val result = startDialogWIthUserPrompt(openAiAssistant, prompt)
        deleteFilesFromOpenAi(openAiAssistant)
        return formatTheResultWithoutMarkdown(result)
    }


    @OptIn(BetaOpenAI::class)
    protected fun startDialogWIthUserPrompt(openAiAssistant: OpenAiAssistant, prompt: String): String {
        val response = runBlocking { openAiAssistantService.runRequest(prompt, openAiAssistant) }
        return if (response.isEmpty()) {
            throw TaskException(TaskStatus.ERROR_ASSISTANT_REPLY)
        } else {
            formResponse(response.first())
        }
    }

    protected fun uploadFile(
        openAiAssistant: OpenAiAssistant, fileContent: InputStream, fileName: String, task: TaskRecord
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

    protected fun formatTheResultWithoutMarkdown(result: String) = result.replace(
        regex = Regex("""【.*】"""),
        ""
    ).replace(regex = Regex("""###|##|#"""), "\n")

    protected fun deleteFilesFromOpenAi(openAiAssistant: OpenAiAssistant) {
        openAiAssistantService.deleteVectorStoreFromAssistant(openAiAssistant)
        log.info("Successfully deleted all filer for $openAiAssistant")
    }

    @OptIn(BetaOpenAI::class)
    private fun formResponse(message: Message): String {
        val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
        return textContent.text.value
    }

    protected abstract fun createAssistant(task: T, taskRecord: TaskRecord): OpenAiAssistant

}