package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.keyboard.inlineKeyboadImp.DOC_TYPE_CALLBACK
import org.superapp.negotiatorbot.botclient.reply.PromptTypeReply
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgUserService
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

private val log = KotlinLogging.logger {}

@Component
class DocumentTypeHandlerQuery(
    senderService: SenderService,
    private val tgUserService: TgUserService,
    private val promptTypeReply: PromptTypeReply
) :
    AbstractCallbackQueryHandler(senderService) {

    override fun handleQuery(query: CallbackQuery) {
        val docType = query.data.toDocumentType()
        log.info("Got document type $docType from user TG id:  ${query.from.id}")
        tgUserService.findByTgId(query.from.id)?.let {
            tgUserService.updateDocumentType(it, docType)
        }
        val message = promptTypeReply.message(query.message.chatId)
        senderService.execute(message)
    }

    private fun String.toDocumentType(): DocumentType {
        return DocumentType.valueOf(this.removePrefix(DOC_TYPE_CALLBACK))
    }

}