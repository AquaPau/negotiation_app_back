package org.superapp.negotiatorbot.botclient.handler.commandhandler

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.model.TgUser
import org.superapp.negotiatorbot.botclient.reply.StartReply
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgUserService
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class StartCommand(
    private val senderService: SenderService,
    private val startReply: StartReply,
    private val tgUserService: TgUserService,
) : AbstractCommand("start", "Начало работы с ботом") {

    override fun execute(message: Message) {
        val from = message.from!!
        createOrUpdateTgUser(from)
        val replyMessage = startReply.message(message.chatId)
        senderService.execute(replyMessage)
    }

    private fun createOrUpdateTgUser(from: User) {
        (tgUserService.findByTgId(from.id)?.let {
            clearTypes(it)
        }
            ?: tgUserService.create(from))
    }

    private fun clearTypes(user: TgUser) {
        tgUserService.clearTypes(user)
    }
}