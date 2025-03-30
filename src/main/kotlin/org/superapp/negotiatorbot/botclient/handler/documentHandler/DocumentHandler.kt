package org.superapp.negotiatorbot.botclient.handler.documentHandler

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.config.BotConfig
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgUserService
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.telegram.telegrambots.meta.api.methods.GetFile
import  org.telegram.telegrambots.meta.api.objects.Document
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.message.Message
import java.net.URI
import java.net.URL

@Service
class DocumentHandler(
    private val senderService: SenderService,
    private val botConfig: BotConfig,
    private val tgUserService: TgUserService,
) {

    private val text = "Ваш документ отправлен на анализ. Пожалуйста поджождите"

    fun handle(message: Message) {
        message.from?.let {
            val document = message.document
            createUrL(document)
            senderService.send(text, message.chatId)
        }
    }

    private fun uploadToAssistant(message: Message) {

    }


    private fun configurePrompt(docType: DocumentType, promptType: PromptType) {
        TODO()
    }

    private fun User.getPair(): Pair<DocumentType, PromptType>? {
        return tgUserService.findByTgId(this.id)?.let {
            tgUserService.getTypes(it)
        }
    }

}