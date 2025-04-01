package org.superapp.negotiatorbot.botclient.response

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.keyboard.createReplyMessageWithKeyboard
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.message.Message

class DocumentUploadResponse(
    private val mappingQuery: String,
) {
    private val replyText =
        "Пожалуйста выберите тип документа"
    fun message(chatId: Long,  messageToReplyId: Int,): BotApiMethod<Message> = createReplyMessageWithKeyboard(
        chatId,
        messageToReplyId,
        replyText,
        TODO()
    )
}