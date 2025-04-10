package org.superapp.negotiatorbot.botclient.view.keyboard

interface InlineKeyboardOption {
    fun userView(): String
    fun callBackData(): String
}