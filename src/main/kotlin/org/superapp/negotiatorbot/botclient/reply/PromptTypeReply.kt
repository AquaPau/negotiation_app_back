package org.superapp.negotiatorbot.botclient.reply

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.keyboard.createMessageWithKeyboard
import org.superapp.negotiatorbot.botclient.keyboard.inlineKeyboadImp.PromptTypeOption
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class PromptTypeReply(
    private val promptTypeOptions: List<PromptTypeOption>
) {
    private val replyText = "Пожалуйста, выберите тип анализа. Для возврата к выбору типа документа отправьте /start"

    fun message(chatId: Long): BotApiMethod<Message> = createMessageWithKeyboard(
        chatId,
        replyText,
        promptTypeOptions
    )
}