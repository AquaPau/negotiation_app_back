package org.superapp.negotiatorbot.botclient.keyboard

import org.superapp.negotiatorbot.botclient.handler.callbackhandler.CallbackQueryHandler


abstract class KeyBoardWithHandler(val callbackQueryHandler: CallbackQueryHandler) : InlineKeyboardOption {
}