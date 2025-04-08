package org.superapp.negotiatorbot.botclient.handler.documentHandler

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.exception.DocumentNotReadyForAssistantException
import org.superapp.negotiatorbot.botclient.handler.commandhandler.StartCommand
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgDocumentService
import org.superapp.negotiatorbot.botclient.view.response.PromptTypeQuestion
import org.telegram.telegrambots.meta.api.objects.Document
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message

@Service
class DocumentHandler(
    private val tgDocumentService: TgDocumentService,
    private val senderService: SenderService,
    private val promptTypeQuestion: PromptTypeQuestion,
    private val startCommand: StartCommand
) {

    fun handle(message: Message) {
        try {
            val tgDocument = getTgDocument(message.chat)
            tgDocument.addDocument(message.document)
            tgDocument.addReplyTo(message.messageId)
            sendPromptQuestion(tgDocument)
        } catch (ignored: DocumentNotReadyForAssistantException) {
            startCommand.execute(message)
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

    private fun sendPromptQuestion(document: TgDocument) {
        val message = promptTypeQuestion.message(document)
        senderService.execute(message)
    }


}