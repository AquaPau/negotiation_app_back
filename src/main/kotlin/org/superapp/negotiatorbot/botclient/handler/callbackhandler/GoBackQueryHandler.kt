package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.handler.commandhandler.StartCommand
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.view.keyboard.InlineKeyboardOption
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

@Component
class GoBackQueryHandler(
    senderService: SenderService,
    private val startCommand: StartCommand
) : AbstractCallbackQueryHandler(senderService) {

    override fun handleQuery(query: CallbackQuery) {
        startCommand.execute(query)
    }

    override fun mappingQuery(): String = "backToStart"

    fun createBackButton(): InlineKeyboardOption = object : InlineKeyboardOption {
        override fun userView(): String =
            "Вернуться к началу"


        override fun callBackData(): String =
            mappingQuery()
    }
}