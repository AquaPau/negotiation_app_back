package org.superapp.negotiatorbot.botclient.view.response

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.view.keyboard.createMessage
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class DocumentUploadQuestion {

    fun message(tgDocument: TgDocument): BotApiMethod<Message> {
        return createMessage(
            chatId = tgDocument.chatId,
            messageText = createReplyMessageText(tgDocument),
        )
    }

    companion object {
        private fun createReplyMessageText(tgDocument: TgDocument): String {
            return """
            Пожалуйста, загрузите договор для анализа.
            Принимаются только документы в форматах .doc, .docx, .txt, .pdf (не скан).
            Документ не может превышать размер в 10 мб.
            Выбранный тип договора: ${tgDocument.chosenDocumentType?.getContractName() ?: "не выбран"}
        """.trimIndent()
        }
    }
}
