package org.superapp.negotiatorbot.botclient.keyboard

interface InlineKeyboardOption {
    fun userView(): String
    fun callBackData(): String
}