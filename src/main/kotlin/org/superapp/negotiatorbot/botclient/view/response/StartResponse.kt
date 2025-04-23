package org.superapp.negotiatorbot.botclient.view.response

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.FaqQueryHandler
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
    private val startDocumentAnalyzeQueryHandler: StartDocumentAnalyzeQueryHandler,
    private val faqQueryHandler: FaqQueryHandler
) {


    fun message(chatId: Long): BotApiMethod<Message> = SendMessage.builder()
        .chatId(chatId)
        .text(WELCOME_TEXT)
        .replyMarkup(InlineKeyboardMarkup(keyboard()))
        .build()

    private fun keyboard(): List<InlineKeyboardRow> {
        return listOf(
            InlineKeyboardRow(
                InlineKeyboardButton.builder()
                    .text(ANALYSE_ACTION)
                    .callbackData(startDocumentAnalyzeQueryHandler.mappingQuery())
                    .build()
            ),
            InlineKeyboardRow(
                InlineKeyboardButton.builder()
                    .text(FAQ_ACTION)
                    .callbackData(faqQueryHandler.mappingQuery())
                    .build()
            ),
            InlineKeyboardRow(
                InlineKeyboardButton.builder()
                    .text(TO_SITE_ACTION)
                    .url(SITE_URL)
                    .callbackData(sentToWebQueryHandler.mappingQuery())
                    .build(),
                InlineKeyboardButton.builder()
                    .text(TO_DESCRIPTION)
                    .url(DESCRIPTION_URL)
                    .callbackData(sentToWebQueryHandler.mappingQuery())
                    .build()
            )
        )
    }

    companion object {
        private const val WELCOME_TEXT = "Добро пожаловать в Legentum - бот для анализа ваших документов."
        private const val SITE_URL = "https://negotiation-web-aquapau.amvera.io/"
        private const val TO_SITE_ACTION = "Сайт"
        private const val ANALYSE_ACTION = "Проанализировать документ"
        private const val FAQ_ACTION = "F.A.Q."
        private const val TO_DESCRIPTION = "О приложении"
        private const val DESCRIPTION_URL = "https://negotiation-web-aquapau.amvera.io/faq"
    }
}

