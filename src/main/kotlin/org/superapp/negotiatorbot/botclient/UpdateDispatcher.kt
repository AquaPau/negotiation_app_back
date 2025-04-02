package org.superapp.negotiatorbot.botclient

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.CallbackQueryHandler
import org.superapp.negotiatorbot.botclient.handler.commandhandler.CommandHandler
import org.superapp.negotiatorbot.botclient.handler.documentHandler.DocumentHandler
import org.superapp.negotiatorbot.botclient.service.QueryMappingService
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.message.Message

private val logger = KotlinLogging.logger {}

@Service
class UpdateDispatcher(
    private val commandHandler: CommandHandler,
    private val documentHandler: DocumentHandler,
    private val queryMappingService: QueryMappingService,
    inlineOptions: List<CallbackQueryHandler>
) {

    private val handlers: Map<String, CallbackQueryHandler> =
        inlineOptions
            .toSet()
            .apply { require(this.size == inlineOptions.size) { "Duplicate key found in $inlineOptions" } }
            .associateBy { it.mappingQuery() }

    private val commandStart = "/"

    fun dispatch(update: Update) {
        update.message?.let { dispatchMessage(it) }
        update.callbackQuery?.let { dispatchCallback(it) }
    }

    private fun dispatchCallback(callbackQuery: CallbackQuery) {
        logger.info { "Dispatching callback: ${callbackQuery.data}" }
        getHandler(callbackQuery).handleResponse(callbackQuery)
    }

    private fun getHandler(query: CallbackQuery): CallbackQueryHandler =
        handlers[queryMappingService.toMapQuery(query.data)]
            ?: throw IllegalStateException("Unknown callback query: ${query.data} known [${handlers.keys}]")


    private fun dispatchMessage(message: Message) {
        when {
            message.isCommand -> commandHandler.handle(message.getCommandName(), message)
            message.hasDocument() -> documentHandler.handle(message)
            else -> unknownMessageType(message)
        }
    }

    private fun unknownMessageType(message: Message) {
        logger.info("Unknown message: $message")
    }

    private fun Message.getCommandName(): String = this.text.substringAfter(commandStart)

}