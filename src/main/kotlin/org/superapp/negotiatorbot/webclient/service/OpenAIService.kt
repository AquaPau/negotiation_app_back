package org.superapp.negotiatorbot.webclient.service

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class OpenAIService(val client: OpenAI, val modelId: ModelId) {

    suspend fun userRoleStringPrompt(prompt: String): String? {
        log.debug("Making req with prompt [{}]", prompt)
        val chatCompletionRequest = getRequest(prompt)
        val response = client.chatCompletion(chatCompletionRequest)
        log.debug("Response [{}]", response)
        return response.choices.first().message.content
    }

    private fun getRequest(prompt: String) = ChatCompletionRequest(
        model = modelId,
        messages = listOf(
            ChatMessage(
                role = ChatRole.User,
                content = prompt
            )
        )
    )
}