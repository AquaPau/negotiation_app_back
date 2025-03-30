package org.superapp.negotiatorbot.botclient.service.openAi

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.config.BotConfig
import org.superapp.negotiatorbot.botclient.exception.DocumentNotAttached
import org.superapp.negotiatorbot.botclient.model.TgChat
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.exception.TaskException
import org.superapp.negotiatorbot.webclient.service.functionality.AssistantService
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskRecordService
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.objects.Document
import java.io.InputStream
import java.net.URI
private val log = KotlinLogging.logger {}

interface OpenAiTgChatService {
    fun provideResponseFromOpenAi(taskRecord: TaskRecord, chat: TgChat, prompt: String): String

}

@Service
class OpenAiTgChatServiceImpl(
    private val tgChatAssistantService: TgChatAssistantService,
    private val senderService: SenderService,
    private val botConfig: BotConfig,
    private val taskRecordService: TaskRecordService,
) : OpenAiTgChatService {
    override fun provideResponseFromOpenAi(taskRecord: TaskRecord, chat: TgChat, prompt: String): String {
        val openAiAssistant = createChatAssistant(chat, taskRecord)
        val result = startDialogWIthUserPrompt(openAiAssistant, prompt)

        TODO("Not yet implemented")
    }

    private fun createChatAssistant(chat: TgChat,taskRecord: TaskRecord): OpenAiAssistant {
        val openAiAssistant = tgChatAssistantService.getAssistant(chat.tgUserId)
        val document = chat.document ?: throw DocumentNotAttached("Document was not attached to this chat: $chat")
        uploadFile(openAiAssistant, downloadDocument(document),document.fileName,taskRecord)
        deleteFilesFromOpenAi(openAiAssistant)
        return openAiAssistant;
    }

    private fun downloadDocument(document: Document): InputStream {
        val getFile = GetFile(document.fileId)
        return URI(senderService.downLoadTgFile(getFile).getFileUrl(botConfig.token)).toURL().openStream()
    }

    private fun uploadFile(
        openAiAssistant: OpenAiAssistant,
        fileContent: InputStream,
        fileName: String,
        task: TaskRecord
    ) {
        try {
            tgChatAssistantService.uploadFile(openAiAssistant, fileContent, fileName)
            log.info("Successfully uploaded file [$fileName] to assistant id:[${openAiAssistant.id}]")
            taskRecordService.changeStatus(task, TaskStatus.SENT_TO_ASSISTANT)
        } catch (ignored: Exception) {
            log.error(ignored) { "Failed to upload file [$fileName] to assistant id:[${openAiAssistant.id}]" }
            throw TaskException(TaskStatus.ERROR_UPLOADING_TO_ASSISTANT)
        }
    }

    @OptIn(BetaOpenAI::class)
    private fun startDialogWIthUserPrompt(openAiAssistant: OpenAiAssistant, prompt: String): String {
        val response = runBlocking { openAiAssistantService.runRequest(prompt, openAiAssistant) }
        return if (response.isEmpty()) {
            throw TaskException(TaskStatus.ERROR_ASSISTANT_REPLY)
        } else {
            formResponse(response.first())
        }
    }

    private fun deleteFilesFromOpenAi(openAiAssistant: OpenAiAssistant) {
        openAiAssistantService.deleteVectorStoreFromAssistant(openAiAssistant)
        log.info("Successfully deleted all filer for $openAiAssistant")
    }

    @OptIn(BetaOpenAI::class)
    private fun formResponse(message: Message): String {
        val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
        return textContent.text.value
    }

}