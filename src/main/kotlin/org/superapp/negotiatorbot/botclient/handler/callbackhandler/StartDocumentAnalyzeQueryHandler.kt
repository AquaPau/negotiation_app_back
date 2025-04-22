package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.model.TgUser
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgDocumentService
import org.superapp.negotiatorbot.botclient.service.TgUserService
import org.superapp.negotiatorbot.botclient.view.response.DocumentTypeQuestion
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat

@Component
class StartDocumentAnalyzeQueryHandler(
    senderService: SenderService,
    private val tgUserService: TgUserService,
    private val tgDocumentService: TgDocumentService,
    private val documentTypeQuestion: DocumentTypeQuestion
) : AbstractCallbackQueryHandler(senderService) {

    override fun handleQuery(query: CallbackQuery) {
        val currentChat = query.message.chat
        cleanUnfinishedDocuments(currentChat)
        val tgDocument = createTgDocument(currentChat, query.from)
        sendDocumentTypeQuestion(tgDocument)
    }

    private fun cleanUnfinishedDocuments(chat: Chat) {
        tgDocumentService.deleteUnfinishedDocuments(chat.id)
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

    override fun mappingQuery(): String = "startDocAnalyze"
}