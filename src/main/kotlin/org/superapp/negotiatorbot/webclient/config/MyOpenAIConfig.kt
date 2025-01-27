package org.superapp.negotiatorbot.webclient.config

import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MyOpenAIConfig(
    @Value("\${open-ai.token}") val token: String,
) {
    @Bean
    fun openAIClient(): OpenAI {
        val config = OpenAIConfig(
            token = token
        )
        return OpenAI(config)
    }


}