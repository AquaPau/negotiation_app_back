package org.superapp.negotiatorbot.botclient.service

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.File
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.io.Serializable

@Service
class SenderService(val telegramClient: TelegramClient) {

    fun send(message: String, chatId: Long): Message {
        return doSendTextMessage(message, chatId, false)
    }

    fun <T : Serializable> execute(method: BotApiMethod<T>) = telegramClient.execute(method)

    fun downloadTgFile(fileMethod: BotApiMethod<File>): File = telegramClient.execute(fileMethod)

    private fun doSendTextMessage(txt: String, groupId: Long, format: Boolean): Message {
        val sendMessage = SendMessage(groupId.toString(), txt)
        sendMessage.enableMarkdown(format)
        return telegramClient.execute(sendMessage)
    }
}