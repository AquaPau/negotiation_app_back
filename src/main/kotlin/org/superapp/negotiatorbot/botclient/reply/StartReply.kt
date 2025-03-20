package org.superapp.negotiatorbot.botclient.reply

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.keyboard.InlineKeyboardOption
import org.superapp.negotiatorbot.botclient.keyboard.createMessageWithKeyboard
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class StartReply {
    private val replyText = "Этот бот поможет проанализировать документы. Пожалуйста выбарите тип документа "

    fun message(chatId: Long): BotApiMethod<Message> = createMessageWithKeyboard(
        chatId,
        replyText,
        DocumentOptions.LABOUR_CONTRACT
    )


    private enum class DocumentOptions(private val userView: String) : InlineKeyboardOption {
        LABOUR_CONTRACT("Трудовой договор");

        override fun callBackData(): String = name
        override fun userView(): String = userView
    }
}

