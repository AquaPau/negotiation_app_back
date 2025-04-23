package org.superapp.negotiatorbot.botclient.view.response

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.view.keyboard.createMessage
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class SendToAssistantResponse {
    fun message(tgDocument: TgDocument): BotApiMethod<Message> {
        return createMessage(
            chatId = tgDocument.chatId,
            messageText = createReplyMessageText(tgDocument),
        )
    }

    companion object {
        private fun createReplyMessageText(tgDocument: TgDocument): String {

            return """
            Ваш договор загружен для анализа. 
            Пожалуйста, ожидайте окончания анализа, он может занять до 2х минут.
            Имя договора: ${tgDocument.tgDocumentName}
            Выбранный тип договора: ${tgDocument.chosenDocumentType?.getContractName() ?: "не выбран"}
            Выбранный тип стороны договора: ${tgDocument.chosenCounterpartyType?.getCounterpartyName() ?: "не выбрана"}
        """.trimIndent()
        }
    }
}