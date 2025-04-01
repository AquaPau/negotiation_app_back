package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.dto.ChosenDocumentOption
import org.superapp.negotiatorbot.botclient.response.PromptTypeResponse
import org.superapp.negotiatorbot.botclient.service.QueryMappingService
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgDocumentService
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

private val log = KotlinLogging.logger {}

@Component
class DocumentTypeQueryHandler(
    senderService: SenderService,
    private val tgDocumentService: TgDocumentService,
    private val queryMappingService: QueryMappingService,
    private val promptTypeResponse: PromptTypeResponse
) : AbstractCallbackQueryHandler(senderService) {

    override fun handleQuery(query: CallbackQuery) {
        val chosenDocumentOption = query.data.toChosenDocumentOption()
        log.info("Got document type $chosenDocumentOption from user TG id:  ${query.from.id}")
        val tgDocument =
            tgDocumentService.updateDocumentType(chosenDocumentOption.tgDocumentId, chosenDocumentOption.documentType)
        val message = promptTypeResponse.message(tgDocument)
        senderService.execute(message)
    }

    private fun String.toChosenDocumentOption(): ChosenDocumentOption =
        queryMappingService.getPayload(this, ChosenDocumentOption::class.java)

}