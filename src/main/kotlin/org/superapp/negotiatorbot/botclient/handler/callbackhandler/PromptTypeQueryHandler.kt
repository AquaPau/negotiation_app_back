package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.dto.ChosenPromptOption
import org.superapp.negotiatorbot.botclient.service.AnalyseResultSender
import org.superapp.negotiatorbot.botclient.service.QueryMappingService
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgDocumentService
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

private val log = KotlinLogging.logger {}

@Component
class PromptTypeQueryHandler(
    senderService: SenderService,
    private val queryMappingService: QueryMappingService,
    private val tgDocumentService: TgDocumentService,
    private val analyseResultSender: AnalyseResultSender
) : AbstractCallbackQueryHandler(senderService) {

    override fun mappingQuery(): String {
        return "PromptType"
    }

    override fun handleQuery(query: CallbackQuery) {
        val chosenPromptType = query.data.toPromptOption()
        log.info("Got prompt type $chosenPromptType from user TG id:  ${query.from.id}")
        val tgDocument =
            tgDocumentService.updatePromptType(chosenPromptType.tgDocumentDbId, chosenPromptType.promptType)
        GlobalScope.launch {
            analyseResultSender.analyseAndSendRelatedMessages(tgDocument)
        }
    }

    private fun String.toPromptOption(): ChosenPromptOption =
        queryMappingService.getPayload(this, ChosenPromptOption::class.java)

}
