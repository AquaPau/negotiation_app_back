package org.superapp.negotiatorbot.botclient

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.handler.commandhandler.CommandHandler
import org.superapp.negotiatorbot.botclient.handler.documentHandler.DocumentHandler
import org.telegram.telegrambots.meta.api.objects.message.Message

private val logger = KotlinLogging.logger {}

@Service
class MessageDispatcher(
    private val commandHandler: CommandHandler,
    private val documentHandler: DocumentHandler

) {
    val commandStart = "/"
    fun dispatch(message: Message) {
        when {
            message.isCommand -> commandHandler.handle(message.getCommandName(), message)
            message.hasDocument() -> documentHandler.hande(message.document)
            else -> unknownMessageType(message)
        }
    }

    private fun unknownMessageType(message: Message) {
        logger.info("Unknown message: $message");
    }

    private fun Message.getCommandName(): String = this.text.substringAfter(commandStart)
}