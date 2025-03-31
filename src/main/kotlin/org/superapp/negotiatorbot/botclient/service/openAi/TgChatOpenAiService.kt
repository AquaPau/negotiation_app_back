package org.superapp.negotiatorbot.botclient.service.openAi

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.config.BotConfig
import org.superapp.negotiatorbot.botclient.exception.DocumentNotAttached
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.service.functionality.AssistantService
import org.superapp.negotiatorbot.webclient.service.functionality.openai.AbstractOpenAiService
import org.superapp.negotiatorbot.webclient.service.functionality.openai.OpenAiAssistantService
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskRecordService
import org.telegram.telegrambots.meta.api.methods.GetFile
import java.io.InputStream
import java.net.URI


@Service
class TgChatOpenAiService(
    openAiAssistantService: OpenAiAssistantService,
    taskService: TaskRecordService,
    private val senderService: SenderService,
    private val botConfig: BotConfig,
    private val tgDocumentAssistantService: AssistantService<TgDocument>,
) : AbstractOpenAiService<TgDocument>(openAiAssistantService, taskService) {

    override fun createAssistant(chat: TgDocument, taskRecord: TaskRecord): OpenAiAssistant {
        val openAiAssistant = tgDocumentAssistantService.getAssistant(chat.tgUserId)
        val tgFileId = chat.tgFileId ?: throw DocumentNotAttached("Document was not attached to this chat: $chat")
        val tgFileName = chat.tgDocumentName ?: throw DocumentNotAttached("Document was not attached to this chat: $chat")
        uploadFile(openAiAssistant, downloadDocument(tgFileId), tgFileName, taskRecord)
        deleteFilesFromOpenAi(openAiAssistant)
        return openAiAssistant;
    }

    private fun downloadDocument(tgFileId: String): InputStream {
        val getFile = GetFile(tgFileId)
        return URI(senderService.downLoadTgFile(getFile).getFileUrl(botConfig.token)).toURL().openStream()
    }
}