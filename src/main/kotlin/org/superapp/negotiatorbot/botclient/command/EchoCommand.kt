package org.superapp.negotiatorbot.botclient.command

import org.springframework.context.annotation.Profile
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
@Profile("telegram")
class EchoCommand(val senderService: SenderService) : AbstractCommand("echo", "returns your message to test stuff") {
    override fun execute(message: Message) {
        senderService.send(message.text, message.chatId)
    }
}