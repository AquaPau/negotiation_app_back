package org.superapp.negotiatorbot.botclient.view.response

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.dto.ChosenDocumentOption
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.DocumentTypeQueryHandler
import org.superapp.negotiatorbot.botclient.model.TelegramDocumentType
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.service.QueryMappingService
import org.superapp.negotiatorbot.botclient.view.keyboard.InlineKeyboardOption
import org.superapp.negotiatorbot.botclient.view.keyboard.createMessageWithKeyboard
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class DocumentTypeQuestion(
    val queryMappingService: QueryMappingService,
    val documentTypeQueryHandler: DocumentTypeQueryHandler
) {

    fun formMessage(tgDocument: TgDocument): BotApiMethod<Message> {
        val keyboardFactory = DocumentOptionsFactory(documentTypeQueryHandler.mappingQuery(), tgDocument.id!!)
        return createMessageWithKeyboard(
            chatId = tgDocument.chatId,
            messageText = REPLY_TEXT,
            options = keyboardFactory.createKeyboards()
        )
    }

    private inner class DocumentOptionsFactory(
        private val mappingQuery: String,
        private val tgDocumentDbId: Long,
    ) {

        fun createKeyboards(): List<InlineKeyboardOption> {
            return TelegramDocumentType.entries.map {
                createKeyboard(it)
            }
        }


        fun createKeyboard(documentType: TelegramDocumentType): InlineKeyboardOption {
            val docOption = ChosenDocumentOption(tgDocumentDbId, documentType)
            return object : InlineKeyboardOption {
                override fun userView() = documentType.getContractName()
                override fun callBackData(): String = queryMappingService.toCallbackQuery(mappingQuery, docOption)
            }
        }
    }

    companion object {
        private const val REPLY_TEXT = "Пожалуйста, выберите тип договора"
    }
}

