package org.superapp.negotiatorbot.botclient.response

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.dto.ChosenPromptOption
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.PromptTypeQueryHandler
import org.superapp.negotiatorbot.botclient.keyboard.InlineKeyboardOption
import org.superapp.negotiatorbot.botclient.keyboard.createMessageWithKeyboard
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.service.QueryMappingService
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class PromptTypeQuestion(
    private val queryMappingService: QueryMappingService,
    private val promptTypeQueryHandler: PromptTypeQueryHandler,
    private val typesToViewFactory: TypesToViewFactory,
) {

    fun message(tgDocument: TgDocument): BotApiMethod<Message> {
        val keyBoardFactory = PromptOptionsFactory(promptTypeQueryHandler.mappingQuery(), tgDocument.id!!)
        return createMessageWithKeyboard(
            chatId = tgDocument.chatId,
            messageText = createReplyMessageText(tgDocument),
            options = keyBoardFactory.createKeyboards()
        )
    }

    private fun createReplyMessageText(tgDocument: TgDocument): String {
        val chosenTypeView = typesToViewFactory.viewOf(tgDocument.chosenDocumentType!!)
        return "Выбран тип документа: $chosenTypeView. Пожалуйста, выберите тип анализа"
    }

    private inner class PromptOptionsFactory(
        private val mappingQuery: String,
        private val tgDocumentDbId: Long,
    ) {

        fun createKeyboards(): List<InlineKeyboardOption> {
            return typesToViewFactory.promptTypes.keys.map {
                createKeyboard(it)
            }
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