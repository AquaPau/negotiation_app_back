package org.superapp.negotiatorbot.botclient.keyboard

import org.springframework.lang.NonNull
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow

fun createMessageWithKeyboard(
    @NonNull chatId: Long,
    messageText: String,
    keyboard: ReplyKeyboard
): BotApiMethod<Message> {
    val sendMessage: SendMessage = SendMessage.builder()
        .chatId(chatId)
        .text(messageText)
        .replyMarkup(keyboard)
        .build()
    return sendMessage
}

fun createMessageWithKeyboard(
    @NonNull chatId: Long,
    messageText: String,
    @NonNull options: List<InlineKeyboardOption>
): BotApiMethod<Message> {
    val sendMessage: SendMessage = SendMessage.builder()
        .chatId(chatId)
        .text(messageText)
        .replyMarkup(createReplyKeyboard(options))
        .build()
    return sendMessage
}

/**
 * Vertical keyboard with all listed options
 *
 * @param options options to list
 * @return vertical keyboard
 */
fun createReplyKeyboard(@NonNull options: List<InlineKeyboardOption>): InlineKeyboardMarkup {
    val buttons = options.map { InlineKeyboardRow(createButton(it)) }
    return InlineKeyboardMarkup(buttons)
}


private fun createButton(option: InlineKeyboardOption): InlineKeyboardButton {
    return InlineKeyboardButton.builder().text(option.userView()).callbackData(option.callBackData()).build()
}

