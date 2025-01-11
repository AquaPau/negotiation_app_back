package org.superapp.negotiatorbot.webclient.config

import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "open-ai")
class OpenAIConfig {
    private lateinit var model: String
    private lateinit var token: String

    @Bean
    fun openAIClient(): OpenAI {
        val config = OpenAIConfig(
            token = token
        )
        return OpenAI(config)
    }

    @Bean
    fun defaultModel(): ModelId = ModelId(model)

}