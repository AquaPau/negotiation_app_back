package org.superapp.negotiatorbot.botclient.handler.callbackhandler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.keyboard.inlineKeyboadImp.PROMPT_TYPE_CALLBACK
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.superapp.negotiatorbot.botclient.service.TgUserService
import org.superapp.negotiatorbot.webclient.enum.PromptType
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

private val log = KotlinLogging.logger {}

@Component
class PromptTypeCallbackHandler(
    senderService: SenderService,
    private val tgUserService: TgUserService,
) : AbstractTgCallbackHandler(senderService) {

    override fun handleQuery(query: CallbackQuery) {
        val promptType = query.data.toPromptType()
        log.info("Got prompt type $promptType from user TG id:  ${query.from.id}")
        tgUserService.findByTgId(query.from.id)?.let {
            tgUserService.updatePromptType(it, promptType)
        }
    }

    private fun String.toPromptType(): PromptType {
        return PromptType.valueOf(this.removePrefix(PROMPT_TYPE_CALLBACK))
    }
}