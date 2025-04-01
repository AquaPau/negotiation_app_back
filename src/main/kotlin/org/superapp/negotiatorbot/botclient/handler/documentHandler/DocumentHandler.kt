package org.superapp.negotiatorbot.botclient.handler.documentHandler

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.response.DocumentUploadResponse
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgDocumentService
import org.superapp.negotiatorbot.botclient.service.TgUserService
import org.telegram.telegrambots.meta.api.objects.message.Message

@Service
class DocumentHandler(
    private val tgUserService: TgUserService,
    private val tgDocumentService: TgDocumentService,
    private val senderService: SenderService,
    private val documentUploadResponse: DocumentUploadResponse
) {


    fun handle(message: Message) {
       val tgUserId = tgUserService.getTgUser(message.from).id!!
        tgDocumentService.create(message, tgUserId)
        val reply = documentUploadResponse.message(message.chatId, message.messageId)
        senderService.execute(reply)
    }

}