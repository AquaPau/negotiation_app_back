package org.superapp.negotiatorbot.botclient.service

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.exception.DocumentNotReadyForAssistantException
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.service.openAi.AnalyseTgService
import org.superapp.negotiatorbot.botclient.view.response.SendToAssistantResponse
import java.util.Objects.isNull

@Service
class AnalyseResultSender(
    private val sendToAssistantResponse: SendToAssistantResponse,
    private val analyseTgService: AnalyseTgService,
    private val senderService: SenderService,
) {

    suspend fun analyseAndSendRelatedMessages(tgDocument: TgDocument) {
        tgDocument.sendUploadToAssistantMessage()
        val analysisResult = analyseTgService.analyseDoc(tgDocument)
        senderService.sendReplyText(
            analysisResult,
            chatId = tgDocument.chatId,
            messageToReplyId = tgDocument.messageId!!
        )
    }

    private fun TgDocument.sendUploadToAssistantMessage() {
        validateOrThrow()
        val message = sendToAssistantResponse.message(this)
        senderService.execute(message)
    }


    private fun TgDocument.validateOrThrow() {
        if (isNull(chosenDocumentType) || isNull(chosenPromptType)) {
            throw DocumentNotReadyForAssistantException()
        }
    }
}