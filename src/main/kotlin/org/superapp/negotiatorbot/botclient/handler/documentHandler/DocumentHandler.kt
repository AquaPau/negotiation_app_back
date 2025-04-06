package org.superapp.negotiatorbot.botclient.handler.documentHandler

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.superapp.negotiatorbot.botclient.exception.DocumentNotReadyForAssistantException
import org.superapp.negotiatorbot.botclient.handler.commandhandler.StartCommand
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.response.SendToAssistantResponse
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgDocumentService
import org.superapp.negotiatorbot.botclient.service.openAi.AnalyseTgService
import org.telegram.telegrambots.meta.api.objects.Document
import org.telegram.telegrambots.meta.api.objects.message.Message
import java.util.Objects.isNull

@Service
class DocumentHandler(
    private val tgDocumentService: TgDocumentService,
    private val senderService: SenderService,
    private val sendToAssistantResponse: SendToAssistantResponse,
    private val analyseTgService: AnalyseTgService,
    private val startCommand: StartCommand
) {

    @OptIn(DelicateCoroutinesApi::class)
    @Transactional
    fun handle(message: Message) {
        try {
            val tgDocument = getTgDocument(message)
            tgDocument.addDocument(message.document)
            tgDocument.addReplyTo(message.messageId)
            sendUploadToAssistantMessage(tgDocument)
            GlobalScope.launch {
                tgDocument.analyseAndSendResponseToUser()
            }
        } catch (ignored: DocumentNotReadyForAssistantException) {
            startCommand.execute(message)
        }
    }


    private fun getTgDocument(message: Message): TgDocument {
        if (message.isReply) {
            val prevMessage = message.replyToMessage
            return tgDocumentService.foundByChatIdMessageIdOfDocumentUploadingMessage(
                prevMessage.chatId,
                prevMessage.messageId
            ) ?: throw DocumentNotReadyForAssistantException()
        } else {
            throw DocumentNotReadyForAssistantException()
        }
    }

    private fun TgDocument.addDocument(document: Document) {
        this.validateOrThrow()
        this.addDocument(document)
    }

    private fun TgDocument.addReplyTo(messageId: Int) {
        tgDocumentService.addReplyTo(this, messageId)
    }


    private fun sendUploadToAssistantMessage(tgDocument: TgDocument) {
        val message = sendToAssistantResponse.message(tgDocument)
        senderService.execute(message)
    }

    private suspend fun TgDocument.analyseAndSendResponseToUser() {
        val analysisResult = analyseTgService.analyseDoc(this)
        senderService.sendReplyText(analysisResult, chatId = chatId, messageToReplyId = messageId!!)
    }

    private fun TgDocument.validateOrThrow() {
        if (isNull(chosenDocumentType) || isNull(chosenPromptType)) {
            throw DocumentNotReadyForAssistantException()
        }
    }
}