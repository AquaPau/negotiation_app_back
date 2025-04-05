package org.superapp.negotiatorbot.botclient.handler.commandhandler

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class EchoCommand(val senderService: SenderService) : AbstractCommand("echo", "returns your message to test stuff") {
    override fun execute(message: Message) {
        senderService.sendText(message.text, message.chatId)
    }
}