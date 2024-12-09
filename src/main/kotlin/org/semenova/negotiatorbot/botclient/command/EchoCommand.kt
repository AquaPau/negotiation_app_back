package org.semenova.negotiatorbot.botclient.command

import org.semenova.negotiatorbot.botclient.service.SenderService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class EchoCommand(val senderService: SenderService) : AbstractCommand("echo", "returns your message to test stuff") {
    override fun execute(message: Message) {
        senderService.send(message.text, message.chatId)
    }
}