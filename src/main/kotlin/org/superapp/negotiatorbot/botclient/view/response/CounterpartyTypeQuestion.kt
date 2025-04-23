package org.superapp.negotiatorbot.botclient.view.response

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.dto.ChosenCounterpartyOption
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.CounterpartyTypeQueryHandler
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.GoBackQueryHandler
import org.superapp.negotiatorbot.botclient.model.TelegramDocumentCounterpartyType
import org.superapp.negotiatorbot.botclient.model.TelegramDocumentType
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.service.QueryMappingService
import org.superapp.negotiatorbot.botclient.view.keyboard.InlineKeyboardOption
import org.superapp.negotiatorbot.botclient.view.keyboard.createReplyMessageWithKeyboard
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.message.Message

@Component
class CounterpartyTypeQuestion(
    private val queryMappingService: QueryMappingService,
    private val counterpartyTypeQueryHandler: CounterpartyTypeQueryHandler,
    private val goBackQueryHandler: GoBackQueryHandler
) {

    fun message(tgDocument: TgDocument): BotApiMethod<Message> {
        val keyBoardFactory = CounterpartyOptionsFactory(counterpartyTypeQueryHandler.mappingQuery(), tgDocument.id!!)
        return createReplyMessageWithKeyboard(
            chatId = tgDocument.chatId,
            messageToReplyId = tgDocument.messageId!!,
            messageText = createReplyMessageText(tgDocument),
            options = keyBoardFactory.createKeyboards(tgDocument.chosenDocumentType!!)
        )
    }

    private fun createReplyMessageText(tgDocument: TgDocument): String {
        return """
                Пожалуйста, выберите вашу сторону в договоре.
                Имя договора: ${tgDocument.tgDocumentName}
                Выбранный тип договора: ${tgDocument.chosenDocumentType?.getContractName() ?: "Не выбран"}
        """.trimIndent()
    }

    private inner class CounterpartyOptionsFactory(
        private val mappingQuery: String,
        private val tgDocumentDbId: Long,
    ) {

        fun createKeyboards(telegramDocumentType: TelegramDocumentType): List<InlineKeyboardOption> {
            val options = TelegramDocumentCounterpartyType.entries.filter {
                telegramDocumentType.getCounterparties().contains(it)
            }.map {
                createKeyboard(it)
            }.toMutableList()
            options.add(goBackQueryHandler.createBackButton())
            return options
        }

        fun createKeyboard(counterpartyType: TelegramDocumentCounterpartyType): InlineKeyboardOption {
            val counterpartyOption = ChosenCounterpartyOption(tgDocumentDbId, counterpartyType)
            return object : InlineKeyboardOption {
                override fun userView() = counterpartyType.getCounterpartyName()
                override fun callBackData(): String =
                    queryMappingService.toCallbackQuery(mappingQuery, counterpartyOption)
            }
        }
    }
}