package org.superapp.negotiatorbot.botclient.response

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.dto.ChosenPromptOption
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.PromptTypeQueryHandler
import org.superapp.negotiatorbot.botclient.keyboard.InlineKeyboardOption
import org.superapp.negotiatorbot.botclient.keyboard.createReplyMessageWithKeyboard
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.service.QueryMappingService
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class PromptTypeResponse(
    val queryMappingService: QueryMappingService,
    val promptTypeQueryHandler: PromptTypeQueryHandler,
) {
    private val replyText = "Пожалуйста, выберите тип анализа."

    fun message(tgDocument: TgDocument): BotApiMethod<Message> {
        val keyBoardFactory = PromptOptionsFactory(promptTypeQueryHandler.mappingQuery(), tgDocument.id!!)
        return createReplyMessageWithKeyboard(
            chatId = tgDocument.chatId,
            messageToReplyId = tgDocument.messageId,
            messageText = replyText,
            listOf(
                keyBoardFactory.createKeyboard("Описание содержания", PromptType.DESCRIPTION),
                keyBoardFactory.createKeyboard("Анализ рисков", PromptType.RISKS),
            )
        )
    }

    private inner class PromptOptionsFactory(
        private val mappingQuery: String,
        private val tgDocumentDbId: Long,
    ) {
        fun createKeyboard(userView: String, promptType: PromptType): InlineKeyboardOption {
            val promptOption = ChosenPromptOption(tgDocumentDbId, promptType)
            return object : InlineKeyboardOption {
                override fun userView() = userView
                override fun callBackData(): String = queryMappingService.toCallbackQuery(mappingQuery, promptOption)
            }
        }
    }
}