package org.superapp.negotiatorbot.botclient.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.generics.TelegramClient

@Configuration
class BotConfig( @Value("\${telegram.token}") val token: String) {
    @Bean
    @Profile("telegram")
    fun telegramClient() = OkHttpTelegramClient(token)
}