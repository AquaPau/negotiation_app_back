package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import org.telegram.telegrambots.meta.api.objects.CallbackQuery

interface TGCallbackHandler {
    fun handleResponse(callbackQuery: CallbackQuery)
}