package org.superapp.negotiatorbot.botclient.response

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.dto.ChosenDocumentOption
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.DocumentTypeQueryHandler
import org.superapp.negotiatorbot.botclient.keyboard.InlineKeyboardOption
import org.superapp.negotiatorbot.botclient.keyboard.createMessageWithKeyboard
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.service.QueryMappingService
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class DocumentTypeQuestion(
    val queryMappingService: QueryMappingService,
    val documentTypeQueryHandler: DocumentTypeQueryHandler,
    val typesToViewFactory: TypesToViewFactory
) {
    private val replyText =
        "Пожалуйста выберите тип документа"

    fun formMessage(tgDocument: TgDocument): BotApiMethod<Message> {
        val keyboardFactory = DocumentOptionsFactory(documentTypeQueryHandler.mappingQuery(), tgDocument.id!!)
        return createMessageWithKeyboard(
            chatId = tgDocument.chatId,
            messageText = replyText,
            options = keyboardFactory.createKeyboards()
        )
    }

    private inner class DocumentOptionsFactory(
        private val mappingQuery: String,
        private val tgDocumentDbId: Long,
    ) {

        fun createKeyboards(): List<InlineKeyboardOption> {
            return typesToViewFactory.documentTypes.keys.map {
                createKeyboard(it)
            }
        }


        fun createKeyboard(documentType: DocumentType): InlineKeyboardOption {
            val userView = typesToViewFactory.viewOf(documentType)
            val docOption = ChosenDocumentOption(tgDocumentDbId, documentType)
            return object : InlineKeyboardOption {
                override fun userView() = userView
                override fun callBackData(): String = queryMappingService.toCallbackQuery(mappingQuery, docOption)
            }
        }
    }
}

