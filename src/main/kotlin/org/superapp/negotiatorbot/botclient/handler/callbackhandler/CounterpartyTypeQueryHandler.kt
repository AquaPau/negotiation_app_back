package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.dto.ChosenCounterpartyOption
import org.superapp.negotiatorbot.botclient.service.AnalyseResultSender
import org.superapp.negotiatorbot.botclient.service.QueryMappingService
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgDocumentService
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

private val log = KotlinLogging.logger {}

@Component
class CounterpartyTypeQueryHandler(
    senderService: SenderService,
    private val queryMappingService: QueryMappingService,
    private val tgDocumentService: TgDocumentService,
    private val analyseResultSender: AnalyseResultSender
) : AbstractCallbackQueryHandler(senderService) {

    override fun mappingQuery(): String {
        return "CounterpartyType"
    }

    override fun handleQuery(query: CallbackQuery) {
        val chosenCounterpartyOption = query.data.toCounterpartyOption()
        log.info("Got counterparty type $chosenCounterpartyOption from user TG id:  ${query.from.id}")
        val tgDocument =
            tgDocumentService.updateCounterpartyType(
                chosenCounterpartyOption.tgDocumentDbId,
                chosenCounterpartyOption.counterparty
            )
        GlobalScope.launch {
            analyseResultSender.analyseAndSendRelatedMessages(tgDocument)
        }
    }

    private fun String.toCounterpartyOption(): ChosenCounterpartyOption =
        queryMappingService.getPayload(this, ChosenCounterpartyOption::class.java)

}
