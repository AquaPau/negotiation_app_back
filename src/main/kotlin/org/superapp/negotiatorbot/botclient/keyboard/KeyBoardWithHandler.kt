package org.superapp.negotiatorbot.botclient.keyboard

import org.superapp.negotiatorbot.botclient.handler.callbackhandler.TgCallbackHandler


abstract class KeyBoardWithHandler(val tgCallbackHandler: TgCallbackHandler) : InlineKeyboardOption {
}