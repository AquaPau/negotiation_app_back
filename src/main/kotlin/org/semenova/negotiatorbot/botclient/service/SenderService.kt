package org.semenova.negotiatorbot.botclient.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard
import org.telegram.telegrambots.meta.generics.TelegramClient

private val log = KotlinLogging.logger {}

@Service
class SenderService(val telegramClient: TelegramClient) {

    fun send(message: String, chatId: Long): Message {
        return doSendTextMessage(message, chatId, false)
    }

    fun sendMd(message: String, chatId: Long): Message {
        return doSendTextMessage(message, chatId, true)
    }

    fun forceReply(message: String, id: Long): Message? {
        val msg = SendMessage(id.toString(), message)
        val forceReplyKeyboard = ForceReplyKeyboard()
        forceReplyKeyboard.forceReply = true
        forceReplyKeyboard.selective = true
        msg.replyMarkup = forceReplyKeyboard
        return telegramClient.execute(msg)
    }


    private fun doSendTextMessage(txt: String, groupId: Long, format: Boolean): Message {
        val sendMessage = SendMessage(groupId.toString(), txt)
        sendMessage.enableMarkdown(format)
        return telegramClient.execute(sendMessage)
    }
}