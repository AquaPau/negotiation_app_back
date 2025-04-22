package org.superapp.negotiatorbot.botclient.handler.commandhandler

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.view.response.StartResponse
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class StartCommand(
    private val senderService: SenderService,
    private val startResponse: StartResponse,
) : AbstractCommand("start", "Начало работы с ботом") {

    override fun execute(message: Message) {
        val currentChat = message.chat
        sendWelcomeMessage(currentChat)
    }

    fun execute(callbackQuery: CallbackQuery) {
        sendWelcomeMessage(callbackQuery.message.chat)
    }

    private fun sendWelcomeMessage(chat: Chat) {
        val replyMessage = startResponse.message(chat.id)
        senderService.execute(replyMessage)
    }

}