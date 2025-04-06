package org.superapp.negotiatorbot.botclient.response

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class StartResponse {
    private val replyText =
        "Этот бот поможет проанализировать документы. Пожалуйста выберите тип документа чтобы начать"

    fun message(chatId: Long): BotApiMethod<Message> = SendMessage.builder()
        .chatId(chatId)
        .text(replyText)
        .build()
}

