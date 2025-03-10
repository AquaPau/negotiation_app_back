package org.superapp.negotiatorbot.webclient.service.functionality.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.content.*
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.entity.task.Task
import org.superapp.negotiatorbot.webclient.entity.task.TaskStatus
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskService
import java.io.InputStream

private val log = KotlinLogging.logger {}

/**
 * User logic umbrella functions for access Open API capabilities
 */

interface OpenAiUserService {
    @Throws(NoSuchElementException::class)
    fun startDialogWIthUserPrompt(openAiAssistant: OpenAiAssistant, prompt: String, task: Task): String
    fun uploadFile(openAiAssistant: OpenAiAssistant, fileContent: InputStream, fileName: String, task: Task)

    fun deleteFilesFromOpenAi(openAiAssistant: OpenAiAssistant)

    fun updateThread(openAiAssistant: OpenAiAssistant)
}

@Service
class OpenAiUserServiceImpl(
    val openAiAssistantService: OpenAiAssistantService,
    val taskService: TaskService,
) : OpenAiUserService {

    @OptIn(BetaOpenAI::class)
    @Throws(NoSuchElementException::class)
    override fun startDialogWIthUserPrompt(openAiAssistant: OpenAiAssistant, prompt: String, task: Task): String {
        val response = runBlocking { openAiAssistantService.runRequest(prompt, openAiAssistant) }

        return if (response.isEmpty()) {
            taskService.changeStatus(task, TaskStatus.ERROR_ASSISTANT_REPLY)
            "try again please"
        } else {
            taskService.changeStatus(task, TaskStatus.SUCCESS_ASSISTANT_REPLY)
            formResponse(response.first())
        }
    }

    override fun uploadFile(openAiAssistant: OpenAiAssistant, fileContent: InputStream, fileName: String, task: Task) {
        try {
            openAiAssistantService.uploadFile(openAiAssistant, fileContent, fileName)
            log.info("Successfully uploaded file [$fileName] to assistant id:[${openAiAssistant.id}]")
            taskService.changeStatus(task, TaskStatus.SENT_TO_ASSISTANT)
        } catch (ignored: Exception) {
            log.error(ignored) { "Failed to upload file [$fileName] to assistant id:[${openAiAssistant.id}]" }
            taskService.changeStatus(task, TaskStatus.ERROR_UPLOADING_TO_ASSISTANT)
        }
    }


    override fun deleteFilesFromOpenAi(openAiAssistant: OpenAiAssistant) {
        openAiAssistantService.deleteVectorStoreFromAssistant(openAiAssistant)
        log.info("Successfully deleted all filer for $openAiAssistant")
    }

    override fun updateThread(openAiAssistant: OpenAiAssistant) {
        openAiAssistantService.updateThread(openAiAssistant)
    }

    @OptIn(BetaOpenAI::class)
    private fun formResponse(message: Message): String {
        val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
        return textContent.text.value
    }

}