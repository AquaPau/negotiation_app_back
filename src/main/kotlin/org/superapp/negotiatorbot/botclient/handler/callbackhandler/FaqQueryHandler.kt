package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow


@Component
class FaqQueryHandler(
    senderService: SenderService,
    private val sentToWebQueryHandler: SentToWebQueryHandler,
    private val startDocumentAnalyzeQueryHandler: StartDocumentAnalyzeQueryHandler
) : AbstractCallbackQueryHandler(senderService) {
    override fun handleQuery(query: CallbackQuery) {
        val message = SendMessage(
            query.message.chatId.toString(),
            FAQ_TEXT
        ).apply {
            replyMarkup = InlineKeyboardMarkup(
                listOf(
                    InlineKeyboardRow(
                        InlineKeyboardButton.builder()
                            .text(ANALYSE_ACTION)
                            .callbackData(startDocumentAnalyzeQueryHandler.mappingQuery())
                            .build()
                    ),
                    InlineKeyboardRow(
                        InlineKeyboardButton.builder()
                            .text(TO_SITE_ACTION)
                            .url(SITE_URL)
                            .callbackData(sentToWebQueryHandler.mappingQuery())
                            .build(),
                        InlineKeyboardButton.builder()
                            .text(TO_START_MENU_ACTION)
                            .callbackData(TO_START_MENU)
                            .build()
                    )
                )
            )
        }

        senderService.execute(message)
    }

    override fun mappingQuery(): String {
        return "FaqType"
    }

    companion object {
        private const val FAQ_TEXT = """
        В данном боте можно получить первичный анализ договора, выбрав его тип и сторону, которую вы представляете. 
        Результат работы бота - чеклист наличия рисков в вашем документе. 
        Полный анализ рисков договора вы можете получить в веб-версии приложения после регистрации"
        """
        private const val SITE_URL = "https://negotiation-web-aquapau.amvera.io/"
        private const val TO_SITE_ACTION = "Сайт"
        private const val ANALYSE_ACTION = "Проанализировать документ"
        private const val TO_START_MENU_ACTION = "На главную"
        private const val TO_START_MENU = "backToStart"

    }


}