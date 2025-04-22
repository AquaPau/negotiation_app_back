package org.superapp.negotiatorbot.botclient.handler.commandhandler

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.model.TgUser
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgDocumentService
import org.superapp.negotiatorbot.botclient.service.TgUserService
import org.superapp.negotiatorbot.botclient.view.response.DocumentTypeQuestion
import org.superapp.negotiatorbot.botclient.view.response.StartResponse
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
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
        val currentChat = message.chat
        cleanUnfinishedDocuments(currentChat)
        sendWelcomeMessage(currentChat)
        val tgDocument = createTgDocument(currentChat, message.from)
        sendDocumentTypeQuestion(tgDocument)
    }

    fun execute(callbackQuery: CallbackQuery) {
        val currentChat = callbackQuery.message.chat
        cleanUnfinishedDocuments(currentChat)
        sendWelcomeMessage(currentChat)
        val tgDocument = createTgDocument(currentChat, callbackQuery.from)
        sendDocumentTypeQuestion(tgDocument)
    }

    private fun cleanUnfinishedDocuments(chat: Chat) {
        tgDocumentService.deleteUnfinishedDocuments(chat.id)
    }

    private fun sendWelcomeMessage(chat: Chat) {
        val replyMessage = startResponse.message(chat.id)
        senderService.execute(replyMessage)
    }

    private fun createTgDocument(chat: Chat, user: User): TgDocument {
        val tgUser = createOrUpdateTgUser(user)
        return tgDocumentService.create(chat, tgUser.id!!)
    }

    private fun createOrUpdateTgUser(from: User): TgUser = tgUserService.getTgUser(from)

    private fun sendDocumentTypeQuestion(tgDocument: TgDocument) {
        val message = documentTypeQuestion.formMessage(tgDocument)
        senderService.execute(message)
    }
}