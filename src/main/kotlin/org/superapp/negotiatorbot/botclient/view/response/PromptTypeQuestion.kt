package org.superapp.negotiatorbot.botclient.view.response

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.dto.ChosenPromptOption
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.GoBackQueryHandler
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.PromptTypeQueryHandler
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.service.QueryMappingService
import org.superapp.negotiatorbot.botclient.view.keyboard.InlineKeyboardOption
import org.superapp.negotiatorbot.botclient.view.keyboard.createReplyMessageWithKeyboard
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class PromptTypeQuestion(
    private val queryMappingService: QueryMappingService,
    private val promptTypeQueryHandler: PromptTypeQueryHandler,
    private val typesToViewFactory: TypesToViewFactory,
    private val goBackQueryHandler: GoBackQueryHandler
) {

    fun message(tgDocument: TgDocument): BotApiMethod<Message> {
        val keyBoardFactory = PromptOptionsFactory(promptTypeQueryHandler.mappingQuery(), tgDocument.id!!)
        return createReplyMessageWithKeyboard(
            chatId = tgDocument.chatId,
            messageToReplyId = tgDocument.messageId!!,
            messageText = createReplyMessageText(tgDocument),
            options = keyBoardFactory.createKeyboards()
        )
    }

    private fun createReplyMessageText(tgDocument: TgDocument): String {
        return """
                Пожалуйста, выберите тип анализа.
                Имя документа: ${tgDocument.tgDocumentName}
                Выбранный тип документа: ${typesToViewFactory.viewOf(tgDocument.chosenDocumentType!!)}
        """.trimIndent()
    }

    private inner class PromptOptionsFactory(
        private val mappingQuery: String,
        private val tgDocumentDbId: Long,
    ) {

        fun createKeyboards(): List<InlineKeyboardOption> {
            val options = typesToViewFactory.promptTypes.keys.map {
                createKeyboard(it)
            }.toMutableList()
            options.add(goBackQueryHandler.createBackButton())
            return options
        }

        fun createKeyboard(promptType: PromptType): InlineKeyboardOption {
            val userView = typesToViewFactory.viewOf(promptType)
            val promptOption = ChosenPromptOption(tgDocumentDbId, promptType)
            return object : InlineKeyboardOption {
                override fun userView() = userView
                override fun callBackData(): String = queryMappingService.toCallbackQuery(mappingQuery, promptOption)
            }
        }
    }
}