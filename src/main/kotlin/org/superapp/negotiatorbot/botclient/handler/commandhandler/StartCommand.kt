package org.superapp.negotiatorbot.botclient.handler.commandhandler

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.response.StartResponse
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgUserService
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class StartCommand(
    private val senderService: SenderService,
    private val startResponse: StartResponse,
    private val tgUserService: TgUserService,
) : AbstractCommand("start", "Начало работы с ботом") {

    override fun execute(message: Message) {
        val from = message.from!!
        createOrUpdateTgUser(from)
        val replyMessage = startResponse.message(message.chatId)
        senderService.execute(replyMessage)
    }

    private fun createOrUpdateTgUser(from: User) {
        tgUserService.getTgUser(from)
    }

}