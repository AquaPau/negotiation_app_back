package org.superapp.negotiatorbot.botclient.service

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.exception.DocumentNotReadyForAssistantException
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.service.openAi.AnalyseTgService
import org.superapp.negotiatorbot.botclient.view.response.SendToAssistantResponse
import org.superapp.negotiatorbot.webclient.exception.CustomUiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow
import java.util.Objects.isNull

@Service
class AnalyseResultSender(
    private val sendToAssistantResponse: SendToAssistantResponse,
    private val analyseTgService: AnalyseTgService,
    private val senderService: SenderService,
) {

    suspend fun analyseAndSendRelatedMessages(tgDocument: TgDocument) {
        tgDocument.sendUploadToAssistantMessage()
        try {
            val analysisResult = analyseTgService.analyseDoc(tgDocument)
            senderService.sendReplyText(
                analysisResult,
                chatId = tgDocument.chatId,
                messageToReplyId = tgDocument.messageId!!
            )
        } catch (e: CustomUiException) {
            val message = SendMessage(
                tgDocument.chatId.toString(),
                e.message
            ).apply {
                replyMarkup = InlineKeyboardMarkup(
                    listOf(
                        InlineKeyboardRow(
                            InlineKeyboardButton.builder()
                                .text(TO_START_MENU_ACTION)
                                .callbackData(TO_START_MENU)
                                .build()
                        )
                    )
                )
            }
            senderService.execute(message)
        } catch (e: Exception) {
            val message = SendMessage(
                tgDocument.chatId.toString(),
                DEFAULT_EXCEPTION_MESSAGE
            ).apply {
                replyMarkup = InlineKeyboardMarkup(
                    listOf(
                        InlineKeyboardRow(
                            InlineKeyboardButton.builder()
                                .text(TO_START_MENU_ACTION)
                                .callbackData(TO_START_MENU)
                                .build()
                        )
                    )
                )
            }
            senderService.execute(message)
        }

    }

    private fun TgDocument.sendUploadToAssistantMessage() {
        validateReadinessDocument()
        val message = sendToAssistantResponse.message(this)
        senderService.execute(message)
    }

    companion object {
        private const val DEFAULT_EXCEPTION_MESSAGE =
            "Произошла ошибка при выполнении запроса. Пожалуйста, попробуйте снова"
        private const val TO_START_MENU_ACTION = "На главную"
        private const val TO_START_MENU = "backToStart"
        private fun TgDocument.validateReadinessDocument() {
            if (isNull(chosenDocumentType) || isNull(chosenCounterpartyType)) {
                throw DocumentNotReadyForAssistantException()
            }
        }
    }
}