package org.superapp.negotiatorbot.webclient.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Bean
    fun appCoroutineScope(): AppCoroutineScope = AppCoroutineScope()
}

class AppCoroutineScope : CoroutineScope by CoroutineScope(Dispatchers.Default)