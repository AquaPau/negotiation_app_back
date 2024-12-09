package org.semenova.negotiatorbot.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.generics.TelegramClient

@Configuration
@PropertySource("classpath:botConfig.properties")
class BotConfig(@Value("\${token}") val token: String) {
    @Bean
    fun telegramClient() = OkHttpTelegramClient(token)
}