package org.superapp.negotiatorbot.botclient.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.File
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.io.Serializable

private val log = KotlinLogging.logger {}

@Service
class SenderService(val telegramClient: TelegramClient) {

    fun sendText(message: String, chatId: Long): Message {
        return doSendTextMessage(message, chatId, false)
    }

    fun sendReplyText(messageText: String, chatId: Long, messageToReplyId: Int): Message {
        val sendMessage: SendMessage = SendMessage.builder()
            .replyToMessageId(messageToReplyId)
            .chatId(chatId)
            .text(messageText)
            .build()
        log.info("Try to Sending message: {}", sendMessage)
        return telegramClient.execute(sendMessage)
    }

    fun <T : Serializable> execute(method: BotApiMethod<T>) {
        log.info("Try to Sending message: {}", method)
        telegramClient.execute(method)
    }

    fun downloadTgFile(fileMethod: BotApiMethod<File>): File {
        log.info("Try to Downloading file: {}", fileMethod)
        return telegramClient.execute(fileMethod)
    }

    private fun doSendTextMessage(txt: String, groupId: Long, format: Boolean): Message {
        val sendMessage = SendMessage(groupId.toString(), txt)
        sendMessage.enableMarkdown(format)
        log.info("Try to Sending message: {}", sendMessage)
        return telegramClient.execute(sendMessage)
    }
}