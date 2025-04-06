package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.dto.ChosenPromptOption
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.view.response.DocumentUploadQuestion
import org.superapp.negotiatorbot.botclient.service.QueryMappingService
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgDocumentService
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.message.Message

private val log = KotlinLogging.logger {}

@Component
class PromptTypeQueryHandler(
    senderService: SenderService,
    private val queryMappingService: QueryMappingService,
    private val tgDocumentService: TgDocumentService,
    private val documentUploadQuestion: DocumentUploadQuestion
) : AbstractCallbackQueryHandler(senderService) {

    override fun mappingQuery(): String {
        return "PromptType"
    }

    override fun handleQuery(query: CallbackQuery) {
        val chosenPromptType = query.data.toPromptOption()
        log.info("Got prompt type $chosenPromptType from user TG id:  ${query.from.id}")
        val tgDocument =
            tgDocumentService.updatePromptType(chosenPromptType.tgDocumentDbId, chosenPromptType.promptType)
        sendDocumentUploadMessage(tgDocument)
    }

    private fun sendDocumentUploadMessage(tgDocument: TgDocument): Message {
        val message = documentUploadQuestion.message(tgDocument)
        return senderService.execute(message)
    }


    private fun String.toPromptOption(): ChosenPromptOption =
        queryMappingService.getPayload(this, ChosenPromptOption::class.java)

}
