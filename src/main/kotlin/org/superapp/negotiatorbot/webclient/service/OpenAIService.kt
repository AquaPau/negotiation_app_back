package org.superapp.negotiatorbot.webclient.service

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.run.RunId
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.client.OpenAI
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.content.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okio.source
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.entity.User
import java.io.InputStream

private val log = KotlinLogging.logger {}

interface OpenAIService {
    @Throws(NoSuchElementException::class)
    fun userPrompt(userId: Long, prompt: String): String?
    suspend fun uploadFile(userId: Long, fileContent: InputStream, fileName: String)
    suspend fun deleteFile(userId: Long)
}

@Service
class OpenAIServiceImpl(
    val openAI: OpenAI,
    val openAiAssistantService: OpenAiAssistantService,
    val userService: UserService,
) : OpenAIService {

    @OptIn(BetaOpenAI::class)
    @Throws(NoSuchElementException::class)
    override fun userPrompt(userId: Long, prompt: String): String {
        val openAiAssistant = getAssistant(userId)
        val response = runBlocking { runRequest(prompt, openAiAssistant) }
        return formResponse(response.first())

    }

    @OptIn(BetaOpenAI::class)
    override suspend fun uploadFile(userId: Long, fileContent: InputStream, fileName: String) {
        val assistantId = getAssistant(userId).getAssistantId()
        val fileId = uploadToOpenAiFile(fileContent, fileName)
        openAiAssistantService.connectFileToAssistant(assistantId, fileId)
    }

    suspend fun uploadToOpenAiFile(fileContent: InputStream, fileName: String): FileId {
        val fileSource = FileSource(name = fileName, source = fileContent.source())
        return openAI.file(
            request = FileUpload(
                file = fileSource,
                purpose = Purpose("assistants")
            )
        ).id
    }

    override suspend fun deleteFile(userId: Long) {
        val assistant = getAssistant(userId)
        openAiAssistantService.deleteVectorStoreFromAssistant(assistant)
        removeOpenAiFile(assistant.fileId)
    }

    suspend fun removeOpenAiFile(fileId: String?) {
        fileId?.let { openAI.delete(FileId(fileId)) }
    }

    @OptIn(BetaOpenAI::class)
    private fun formResponse(message: Message): String {
        val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
        return textContent.text.value
    }

    @Throws(NoSuchElementException::class)
    private fun getAssistant(userId: Long): OpenAiAssistant {
        val user: User =
            userService.findById(userId) ?: throw NoSuchElementException("User with ID $userId does not exists.")
        return openAiAssistantService.getAssistant(user)
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun runRequest(prompt: String, assistant: OpenAiAssistant): List<Message> {
        val threadId = ThreadId(assistant.threadId!!)
        openAI.message(
            threadId = threadId,
            request = MessageRequest(
                role = Role.User,
                content = prompt,
            )
        )

        val run = openAI.createRun(
            threadId,
            request = RunRequest(
                assistantId = AssistantId(assistant.assistantId!!),
            )
        )

        return runRequest(threadId, run.id)
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun runRequest(threadId: ThreadId, runId: RunId): List<Message> {
        log.debug("Started run for thread [${threadId}]")
        do {
            val retrievedRun = openAI.getRun(threadId = threadId, runId = runId)
            delay(1000)
        } while (retrievedRun.status != Status.Completed)
        log.debug("Finished run for thread [${threadId}]")
        return openAI.messages(threadId)
    }

}