package org.superapp.negotiatorbot.botclient.handler.commandhandler

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.model.TgUser
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgDocumentService
import org.superapp.negotiatorbot.botclient.service.TgUserService
import org.superapp.negotiatorbot.botclient.view.response.DocumentTypeQuestion
import org.superapp.negotiatorbot.botclient.view.response.StartResponse
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class StartCommand(
    private val senderService: SenderService,
    private val startResponse: StartResponse,
    private val tgUserService: TgUserService,
    private val tgDocumentService: TgDocumentService,
    private val documentTypeQuestion: DocumentTypeQuestion
) : AbstractCommand("start", "Начало работы с ботом") {

    override fun execute(message: Message) {
        cleanUnfinishedDocuments(message.chat)
        sendWelcomeMessage(message)
        val tgDocument = createTgDocument(message)
        sendDocumentTypeQuestion(tgDocument)
    }

    private fun cleanUnfinishedDocuments(chat: Chat) {
        tgDocumentService.deleteUnfinishedDocuments(chat.id)
    }

    private fun sendWelcomeMessage(message: Message) {
        val replyMessage = startResponse.message(message.chatId)
        senderService.execute(replyMessage)
    }

    private fun createTgDocument(message: Message): TgDocument {
        val tgUser = createOrUpdateTgUser(message.from!!)
        return tgDocumentService.create(message, tgUser.id!!)
    }

    private fun createOrUpdateTgUser(from: User): TgUser = tgUserService.getTgUser(from)

    private fun sendDocumentTypeQuestion(tgDocument: TgDocument) {
        val message = documentTypeQuestion.formMessage(tgDocument)
        senderService.execute(message)
    }
}