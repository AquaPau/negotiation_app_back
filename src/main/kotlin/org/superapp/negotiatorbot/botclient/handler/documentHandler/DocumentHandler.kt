package org.superapp.negotiatorbot.botclient.handler.documentHandler

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgDocumentService
import org.superapp.negotiatorbot.botclient.view.response.CounterpartyTypeQuestion
import org.superapp.negotiatorbot.webclient.exception.CustomUiException
import org.superapp.negotiatorbot.webclient.exception.DocumentExceedsSizeException
import org.superapp.negotiatorbot.webclient.exception.DocumentFormatIsNotAppropriateException
import org.superapp.negotiatorbot.webclient.exception.DocumentNotReadyForAssistantException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Document
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow

@Service
class DocumentHandler(
    private val tgDocumentService: TgDocumentService,
    private val senderService: SenderService,
    private val counterpartyTypeQuestion: CounterpartyTypeQuestion,
) {

    fun handle(message: Message) {
        try {
            val tgDocument = getTgDocument(message.chat)
            message.document.validateSize()
            tgDocument.addDocument(message.document)
            tgDocument.addReplyTo(message.messageId)
            tgDocument.validateExtension()
            sendCounterpartyQuestion(tgDocument)
        } catch (e: CustomUiException) {
            val exceptionMessage = SendMessage(
                message.chatId.toString(),
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
            senderService.execute(exceptionMessage)
        } catch (e: Exception) {
            val exceptionMessage = SendMessage(
                message.chatId.toString(),
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
            senderService.execute(exceptionMessage)
        }
    }


    @Throws(DocumentNotReadyForAssistantException::class)
    private fun getTgDocument(chat: Chat): TgDocument {
        return tgDocumentService.getReadyToUploadDoc(chat.id) ?: throw DocumentNotReadyForAssistantException()
    }


    private fun TgDocument.addDocument(document: Document) {
        tgDocumentService.addDocument(this, document)
    }

    private fun TgDocument.addReplyTo(messageId: Int) {
        tgDocumentService.addReplyTo(this, messageId)
    }

    private fun sendCounterpartyQuestion(document: TgDocument) {
        val message = counterpartyTypeQuestion.message(document)
        senderService.execute(message)
    }

    companion object {
        private val APPROPRIATE_EXTENSIONS = listOf("PDF", "DOC", "DOCX", "TXT")
        private const val DEFAULT_EXCEPTION_MESSAGE =
            "Произошла ошибка при выполнении запроса. Пожалуйста, попробуйте снова"
        private const val MAX_SIZE_IN_BYTES = 10 * 1024 * 1024
        private const val TO_START_MENU_ACTION = "На главную"
        private const val TO_START_MENU = "backToStart"

        private fun TgDocument.validateExtension() {
            val extension = this.tgDocumentName?.split(".")?.lastOrNull()?.uppercase()
            if (extension == null || !APPROPRIATE_EXTENSIONS.contains(extension))
                throw DocumentFormatIsNotAppropriateException()
        }

        private fun Document.validateSize() {
            if (this.fileSize > MAX_SIZE_IN_BYTES) throw DocumentExceedsSizeException()
        }
    }


}