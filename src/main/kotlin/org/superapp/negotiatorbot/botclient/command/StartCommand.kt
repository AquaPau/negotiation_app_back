package org.superapp.negotiatorbot.botclient.command

import org.springframework.context.annotation.Profile
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class StartCommand(val senderService: SenderService) : AbstractCommand("start", "bot entry point") {
    val responseMessage = "This bot can help you to prepare for negotiations";
    override fun execute(message: Message) {
        senderService.send(responseMessage, message.chatId)
    }
}