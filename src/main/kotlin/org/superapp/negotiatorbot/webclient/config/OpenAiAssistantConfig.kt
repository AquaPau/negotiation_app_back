package org.superapp.negotiatorbot.webclient.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "open-ai.assistant")
class OpenAiAssistantConfig {

    lateinit var model: String
    lateinit var instructions: String;
}