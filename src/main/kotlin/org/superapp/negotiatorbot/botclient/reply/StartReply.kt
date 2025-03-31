package org.superapp.negotiatorbot.botclient.reply

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.keyboard.createMessageWithKeyboard
import org.superapp.negotiatorbot.botclient.keyboard.inlineKeyboadImp.DocumentOption
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class StartReply{
    private val replyText = "Этот бот поможет проанализировать документы. Пожалуйста загрузите документ чтобы начать"
}

