package org.superapp.negotiatorbot.botclient.view.response

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.SentToWebQueryHandler
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.StartDocumentAnalyzeQueryHandler
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow

@Component
class StartResponse(
    private val sentToWebQueryHandler: SentToWebQueryHandler,
    private val startDocumentAnalyzeQueryHandler: StartDocumentAnalyzeQueryHandler
) {
    private val replyText =
        "Добро пожаловать в Legentum - бот для анализа ваших документов."
    private val toWebURL = "https://negotiation-web-aquapau.amvera.io/"

    fun message(chatId: Long): BotApiMethod<Message> = SendMessage.builder()
        .chatId(chatId)
        .text(replyText)
        .replyMarkup(InlineKeyboardMarkup(keyboard()))
        .build()

    private fun keyboard(): List<InlineKeyboardRow> {
        val toWeb =
            InlineKeyboardButton.builder()
                .text("Перейти на сайт")
                .url(toWebURL)
                .callbackData(sentToWebQueryHandler.mappingQuery())
                .build()
        val analyze = InlineKeyboardButton.builder()
            .text("Проанализировать документ")
            .callbackData(startDocumentAnalyzeQueryHandler.mappingQuery())
            .build()
        return listOf(InlineKeyboardRow(toWeb), InlineKeyboardRow(analyze))
    }
}

