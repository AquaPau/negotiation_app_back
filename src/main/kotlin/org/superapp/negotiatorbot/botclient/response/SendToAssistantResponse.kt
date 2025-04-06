package org.superapp.negotiatorbot.botclient.response

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.keyboard.createMessage
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class SendToAssistantResponse(
    private val typesToViewFactory: TypesToViewFactory
) {
    fun message(tgDocument: TgDocument): BotApiMethod<Message> {
        return createMessage(
            chatId = tgDocument.chatId,
            messageText = createReplyMessageText(tgDocument),
        )
    }

    private fun createReplyMessageText(tgDocument: TgDocument): String {
        return """
            Ваш документ загружен для анализа. 
            Пожалуйста, ожидайте окончания анализа, он может занять до 2х минут.
            Номер документа: ${tgDocument.id}
            Имя документа: ${tgDocument.tgDocumentName}
            Выбранный тип документа: ${typesToViewFactory.viewOf(tgDocument.chosenDocumentType!!)}
            Выбранный тип анализа: ${typesToViewFactory.viewOf(tgDocument.chosenPromptType!!)}
        """.trimIndent()
    }
}