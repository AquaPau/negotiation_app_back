package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.dto.ChosenPromptOption
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.service.QueryMappingService
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgDocumentService
import org.superapp.negotiatorbot.botclient.service.openAi.AnalyseTgService
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

private val log = KotlinLogging.logger {}

@Component
class PromptTypeQueryHandler(
    senderService: SenderService,
    private val queryMappingService: QueryMappingService,
    private val tgDocumentService: TgDocumentService,
    private val analyseTgService: AnalyseTgService
) : AbstractCallbackQueryHandler(senderService) {

    private val text =
        "Ваш документ анализируется. Пожалуйста ожидайте ответ, он придет в чат. Ответ может занять до 5 минут"

    override fun mappingQuery(): String {
        return "PromptType"
    }

    override fun handleQuery(query: CallbackQuery) {
        val chosenPromptType = query.data.toPromptOption()
        log.info("Got prompt type $chosenPromptType from user TG id:  ${query.from.id}")
        val tgDocument =
            tgDocumentService.updatePromptType(chosenPromptType.tgDocumentDbId, chosenPromptType.promptType)
        senderService.sendReplyText(text, chatId = tgDocument.chatId, messageToReplyId = tgDocument.messageId)
        GlobalScope.launch {
            tgDocument.analyseAndSendResponseToUser()
        }
    }

    private fun String.toPromptOption(): ChosenPromptOption =
        queryMappingService.getPayload(this, ChosenPromptOption::class.java)

    private suspend fun TgDocument.analyseAndSendResponseToUser() {
        val analysisResult = analyseTgService.analyseDoc(this)
        senderService.sendReplyText(analysisResult, chatId = chatId, messageToReplyId = messageId)
    }
}
