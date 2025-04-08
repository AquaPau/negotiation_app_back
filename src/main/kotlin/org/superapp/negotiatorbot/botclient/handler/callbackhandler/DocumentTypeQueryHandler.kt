package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.dto.ChosenDocumentOption
import org.superapp.negotiatorbot.botclient.service.QueryMappingService
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgDocumentService
import org.superapp.negotiatorbot.botclient.view.response.DocumentUploadQuestion
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

private val log = KotlinLogging.logger {}

@Component
class DocumentTypeQueryHandler(
    senderService: SenderService,
    private val tgDocumentService: TgDocumentService,
    private val queryMappingService: QueryMappingService,
    private val documentUploadQuestion: DocumentUploadQuestion,
) : AbstractCallbackQueryHandler(senderService) {
    override fun mappingQuery(): String {
        return "DocType"
    }

    override fun handleQuery(query: CallbackQuery) {
        val chosenDocumentOption = query.data.toChosenDocumentOption()
        log.info("Got document type $chosenDocumentOption from user TG id:  ${query.from.id}")
        val tgDocument =
            tgDocumentService.updateDocumentType(chosenDocumentOption.tgDocumentDbId, chosenDocumentOption.documentType)
        val message = documentUploadQuestion.message(tgDocument)
        senderService.execute(message)
    }

    private fun String.toChosenDocumentOption(): ChosenDocumentOption =
        queryMappingService.getPayload(this, ChosenDocumentOption::class.java)

}