package org.superapp.negotiatorbot.botclient.handler.commandhandler

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.message.Message

abstract class AbstractCommand(val name: String, val description: String) {
    abstract fun execute(message: Message)
    fun toBotCommand() = BotCommand(name, description)
}