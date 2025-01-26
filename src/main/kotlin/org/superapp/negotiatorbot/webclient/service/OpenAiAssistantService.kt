package org.superapp.negotiatorbot.webclient.service

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.Assistant
import com.aallam.openai.api.assistant.AssistantRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadRequest
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.config.OpenAiAssistantConfig
import org.superapp.negotiatorbot.webclient.entity.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.repository.OpenAiAssistantRepository

interface OpenAiAssistantService {
    fun getAssistant(user: User): OpenAiAssistant
}

@Service
class OpenAiAssistantServiceImpl(
    val openAi: OpenAI,
    val openAiAssistantConfig: OpenAiAssistantConfig,
    val openAiAssistantRepository: OpenAiAssistantRepository
) : OpenAiAssistantService {

    override fun getAssistant(user: User): OpenAiAssistant {
        return openAiAssistantRepository.findByUser(user) ?: runBlocking {
            createAssistant(user)
        }
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun createAssistant(user: User): OpenAiAssistant {
        val assistant = openAi.assistant(
            request = AssistantRequest(
                name = "For userid: ${user.id}",
                instructions = openAiAssistantConfig.instructions,
                model = ModelId(openAiAssistantConfig.model),
            )
        )
        val thread = getThread(user)
        return createAssistant(assistant, thread, user)
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun getThread(user: User) = openAi.thread(
        request = ThreadRequest(
            metadata = mapOf(
                "user" to "${user.id}"
            )
        )
    )

    @OptIn(BetaOpenAI::class)
    private fun createAssistant(assistant: Assistant, thread: Thread, user: User): OpenAiAssistant {
        val openAiAssistant = OpenAiAssistant();
        openAiAssistant.user = user
        openAiAssistant.threadId = thread.id.id
        openAiAssistant.assistantId = assistant.id.id
        return openAiAssistantRepository.save(openAiAssistant)
    }
}