package org.superapp.negotiatorbot.botclient.service.openAi

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.model.TgUser
import org.superapp.negotiatorbot.botclient.repository.TgUserRepository
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.service.functionality.AssistantService
import org.superapp.negotiatorbot.webclient.service.functionality.openai.OpenAiAssistantService

@Service
class TgChatAssistantServiceImpl(
    private val tgUserRepository: TgUserRepository,
    private val openAiAssistantService: OpenAiAssistantService
) : AssistantService<TgDocument> {

    @Transactional
    override fun getAssistant(tgUserId: Long): OpenAiAssistant {
        val tgUser = tgUserRepository.findById(tgUserId).orElseThrow()
        return tgUser.assistantDbId?.let {
            getAndUpdateThread(it)
        } ?: createAssistant(tgUser)

    }

    private fun getAndUpdateThread(assistantDbId: Long): OpenAiAssistant {
        val assistant = openAiAssistantService.getAssistant(assistantDbId)
        openAiAssistantService.updateThread(assistant)
        return assistant
    }

    private fun createAssistant(tgUser: TgUser): OpenAiAssistant {
        val assistant = openAiAssistantService.createAssistant()
        tgUser.assistantDbId = assistant.id
        tgUserRepository.save(tgUser)
        return assistant
    }
}