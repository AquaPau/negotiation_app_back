package org.superapp.negotiatorbot.botclient.command

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient

private val logger = KotlinLogging.logger {}

@Service
class CommandHandler(val commands: List<AbstractCommand>, private val client: TelegramClient) {
    private val commandsMap = commands.associateBy { it.name }

    fun handle(commandName: String, message: Message) {
        commandsMap[commandName]?.execute(message) ?: logger.warn { "Unknown command $commandName" }
    }

    @PostConstruct
    private fun registerCommands() {
        client.execute(SetMyCommands(commands.map { it.toBotCommand() }))
    }
}