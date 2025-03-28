package org.superapp.negotiatorbot.botclient

import io.github.oshai.kotlinlogging.KotlinLogging
import org.superapp.negotiatorbot.botclient.config.BotConfig
import org.springframework.stereotype.Component
import org.telegram.telegrambots.longpolling.BotSession
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update


private val logger = KotlinLogging.logger {}

@Component
class NegotiatorBot(val botConfig: BotConfig, val updateDispatcher: UpdateDispatcher) : SpringLongPollingBot,
    LongPollingSingleThreadUpdateConsumer {

    override fun consume(update: Update) {
        logger.info("Got ${update}")
        updateDispatcher.dispatch(update)
    }

    @AfterBotRegistration
    fun afterRegistration(botSession: BotSession) {
        logger.info("Registered bot running state is: ${botSession.isRunning}")
    }

    override fun getBotToken(): String = botConfig.token
    override fun getUpdatesConsumer(): LongPollingUpdateConsumer = this
}
