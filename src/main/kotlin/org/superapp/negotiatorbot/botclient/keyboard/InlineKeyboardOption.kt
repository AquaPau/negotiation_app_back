package org.superapp.negotiatorbot.botclient.keyboard

import org.telegram.telegrambots.meta.api.objects.Update

interface InlineKeyboardOption {
    fun userView(): String
    fun callBackData(): String
}