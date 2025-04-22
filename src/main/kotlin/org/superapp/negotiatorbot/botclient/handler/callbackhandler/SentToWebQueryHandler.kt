package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

private val log = KotlinLogging.logger {}

@Component
class SentToWebQueryHandler(senderService: SenderService) : AbstractCallbackQueryHandler(senderService) {

    override fun handleQuery(query: CallbackQuery) {
        log.info("User send to web")
    }

    override fun mappingQuery(): String = "sendToWeb"
}