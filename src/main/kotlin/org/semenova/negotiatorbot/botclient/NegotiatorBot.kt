package org.semenova.negotiatorbot.botclient

import org.springframework.stereotype.Component
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update

@Component
@Sl4j
class NegotiatorBot: SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    override fun getBotToken(): String {
        TODO("Not yet implemented")
    }

    override fun getUpdatesConsumer(): LongPollingUpdateConsumer = this

    override fun consume(update: Update) {
        TODO("Not yet implemented")
    }
}