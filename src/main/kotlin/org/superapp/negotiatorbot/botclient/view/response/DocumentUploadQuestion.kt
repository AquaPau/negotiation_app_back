package org.superapp.negotiatorbot.botclient.view.response;

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.view.keyboard.createMessage
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
        """.trimIndent()
    }
}
