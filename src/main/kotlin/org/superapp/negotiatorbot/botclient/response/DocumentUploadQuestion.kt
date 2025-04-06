package org.superapp.negotiatorbot.botclient.response;

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.keyboard.createMessage
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class DocumentUploadQuestion(
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
            Пожалуйста, загрузите документ для анализа.
            Выбранный тип документа: ${typesToViewFactory.viewOf(tgDocument.chosenDocumentType!!)}
            Выбранный тип анализа: ${typesToViewFactory.viewOf(tgDocument.chosenPromptType!!)}
        """.trimIndent()
    }
}
