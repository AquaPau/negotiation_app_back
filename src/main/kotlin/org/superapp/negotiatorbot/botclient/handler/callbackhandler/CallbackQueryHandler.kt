package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import org.telegram.telegrambots.meta.api.objects.CallbackQuery

interface CallbackQueryHandler {

    fun handleResponse(callbackQuery: CallbackQuery)
    fun mappingQuery(): String
}