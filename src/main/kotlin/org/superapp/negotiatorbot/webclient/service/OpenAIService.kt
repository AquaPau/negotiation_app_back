package org.superapp.negotiatorbot.webclient.service

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

interface OpenAIService {

    suspend fun userRoleStringPrompt(prompt: String): String?
}

@Service
class OpenAIServiceImpl(
    val client: OpenAI,
    @Value("\${open-ai.model}")
    val model: String
) : OpenAIService {


    override suspend fun userRoleStringPrompt(prompt: String): String? {
        log.debug("Making req with prompt [{}]", prompt)
        val chatCompletionRequest = getRequest(prompt)
        val response = client.chatCompletion(chatCompletionRequest)
        log.debug("Response [{}]", response)
        return response.choices.first().message.content
    }

    private fun getRequest(prompt: String) = ChatCompletionRequest(
        model = ModelId(model),
        messages = listOf(
            ChatMessage(
                role = ChatRole.User,
                content = prompt
            )
        )
    )
}