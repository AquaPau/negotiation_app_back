package org.superapp.negotiatorbot.botclient.keyboard

import org.superapp.negotiatorbot.botclient.handler.callbackhandler.TGCallbackHandler


abstract class KeyBoardWithHandler(val TGCallbackHandler: TGCallbackHandler) : InlineKeyboardOption {
}