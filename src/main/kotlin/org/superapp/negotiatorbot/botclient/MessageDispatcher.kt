package org.superapp.negotiatorbot.botclient

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.superapp.negotiatorbot.botclient.command.CommandHandler
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.message.Message

private val logger = KotlinLogging.logger {}

@Service
class MessageDispatcher(val commandHandler: CommandHandler) {
    val commandStart = "/"
    fun dispatch(message: Message) {
        when {
            message.isCommand -> commandHandler.handle(message.getCommandName(), message)
            else -> unknownMessageType(message)
        }
    }

    private fun unknownMessageType(message: Message) {
        logger.info("Unknown message: $message");
    }

    private fun Message.getCommandName(): String = this.text.substringAfter(commandStart)
}