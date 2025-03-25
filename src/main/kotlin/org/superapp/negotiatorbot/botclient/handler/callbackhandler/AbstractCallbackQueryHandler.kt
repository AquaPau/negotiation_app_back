package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import org.superapp.negotiatorbot.botclient.service.SenderService
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

/**
 * This is used to make sure, that callback queries are closed, and keyboard is deleted in chat, after answer.
 * SHOULD ONLY BE USED WHEN UPDATE HAS CALLBACK IN IT and keyboard!
 */
abstract class AbstractCallbackQueryHandler(
    protected val senderService: SenderService
) : CallbackQueryHandler {

    override fun handleResponse(callbackQuery: CallbackQuery) {
        try {
            handleQuery(callbackQuery)
        } finally {
            callbackQuery.deleteKeyboard()
            callbackQuery.close()
        }
    }

    protected abstract fun handleQuery(query: CallbackQuery)

    private fun CallbackQuery.close() {
        val close = AnswerCallbackQuery.builder().callbackQueryId(this.id).build()
        senderService.execute(close)
    }

    private fun CallbackQuery.deleteKeyboard() {
        val message = this.message
        val editor = EditMessageReplyMarkup.builder()
            .chatId(message.chatId)
            .messageId(message.messageId)
            .build()
        editor.replyMarkup = null //deleting keyboard
        senderService.execute(editor)
    }
}