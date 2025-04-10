package org.superapp.negotiatorbot.botclient.view.keyboard

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow


fun createReplyMessageWithKeyboard(
    chatId: Long,
    messageToReplyId: Int,
    messageText: String,
    options: List<InlineKeyboardOption>
): BotApiMethod<Message> {
    val sendMessage: SendMessage = SendMessage.builder()
        .replyToMessageId(messageToReplyId)
        .chatId(chatId)
        .text(messageText)
        .replyMarkup(createInlineKeyboard(options))
        .build()
    return sendMessage
}

fun createMessageWithKeyboard(
    chatId: Long,
    messageText: String,
    options: List<InlineKeyboardOption>
): BotApiMethod<Message> {
    val sendMessage: SendMessage = SendMessage.builder()
        .chatId(chatId)
        .text(messageText)
        .replyMarkup(createInlineKeyboard(options))
        .build()
    return sendMessage
}

fun createMessage(chatId: Long, messageText: String): BotApiMethod<Message> = SendMessage.builder()
    .chatId(chatId)
    .text(messageText)
    .build()

fun createMessageForceReply(chatId: Long, messageText: String): BotApiMethod<Message> = SendMessage.builder()
    .chatId(chatId)
    .replyMarkup(ForceReplyKeyboard())
    .text(messageText)
    .build()

/**
 * Vertical keyboard with all listed options
 *
 * @param options options to list
 * @return vertical keyboard
 */
fun createInlineKeyboard(options: List<InlineKeyboardOption>): InlineKeyboardMarkup {
    val buttons = options.map { InlineKeyboardRow(createButton(it)) }
    return InlineKeyboardMarkup(buttons)
}


private fun createButton(option: InlineKeyboardOption): InlineKeyboardButton {
    return InlineKeyboardButton.builder().text(option.userView()).callbackData(option.callBackData()).build()
}

