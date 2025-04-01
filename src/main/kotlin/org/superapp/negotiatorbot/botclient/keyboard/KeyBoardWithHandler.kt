package org.superapp.negotiatorbot.botclient.keyboard

import org.superapp.negotiatorbot.botclient.handler.callbackhandler.CallbackQueryHandler


abstract class KeyBoardWithHandler(private val mappingQuery: String) : InlineKeyboardOption {
}