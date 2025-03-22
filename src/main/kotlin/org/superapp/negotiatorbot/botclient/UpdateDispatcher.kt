package org.superapp.negotiatorbot.botclient

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.TgCallbackHandler
import org.superapp.negotiatorbot.botclient.handler.commandhandler.CommandHandler
import org.superapp.negotiatorbot.botclient.handler.documentHandler.DocumentHandler
import org.superapp.negotiatorbot.botclient.keyboard.KeyBoardWithHandler
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.message.Message

private val logger = KotlinLogging.logger {}

@Service
class UpdateDispatcher(
    private val commandHandler: CommandHandler,
    private val documentHandler: DocumentHandler,
    inlineOptions: List<KeyBoardWithHandler>
) {

    private val handlers: Map<String, TgCallbackHandler> =
        inlineOptions.associate { it.callBackData() to it.tgCallbackHandler }

    private val commandStart = "/"

    fun dispatch(update: Update) {
        update.message?.let { dispatchMessage(it) }
        update.callbackQuery?.let { dispatchCallback(it) }
    }

    private fun dispatchCallback(callbackQuery: CallbackQuery) {
        logger.info { "Dispatching callback: ${callbackQuery.data}" }

        handlers.get(callbackQuery.data)?.handleResponse(callbackQuery)
    }

    private fun dispatchMessage(message: Message) {
        when {
            message.isCommand -> commandHandler.handle(message.getCommandName(), message)
            message.hasDocument() -> documentHandler.handle(message.document)
            else -> unknownMessageType(message)
        }
    }

    private fun unknownMessageType(message: Message) {
        logger.info("Unknown message: $message");
    }

    private fun Message.getCommandName(): String = this.text.substringAfter(commandStart)

}