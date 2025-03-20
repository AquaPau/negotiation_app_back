package org.superapp.negotiatorbot.botclient.handler.commandhandler

import org.superapp.negotiatorbot.botclient.service.SenderService
import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.reply.StartReply
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class StartCommand(
    private val senderService: SenderService,
    private val startReply: StartReply,

    ) : AbstractCommand("start", "bot entry point") {

    override fun execute(message: Message) {
        val replyMessage = startReply.message(message.chatId)
        senderService.execute(replyMessage)
    }
}