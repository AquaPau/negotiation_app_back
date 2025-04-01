package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.response.PromptTypeReply
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgDocumentService
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

private val log = KotlinLogging.logger {}

@Component
class DocumentTypeQueryHandler(
    senderService: SenderService,
    private val tgDocumentService: TgDocumentService,
    private val promptTypeReply: PromptTypeReply
) :
    AbstractCallbackQueryHandler(senderService) {

    override fun handleQuery(query: CallbackQuery) {
        val docType = query.data.toDocumentType()
        log.info("Got document type $docType from user TG id:  ${query.from.id}")
        val chatId = query.message.chatId;

        TODO()
        val message = promptTypeReply.message(query.message.chatId)
        senderService.execute(message)
    }

    override fun mappingQuery(): String {
        TODO("Not yet implemented")
    }

    private fun String.toDocumentType(): DocumentType {
        TODO()
    }

}