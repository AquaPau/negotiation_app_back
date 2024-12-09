package org.semenova.negotiatorbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

// Place definition above class declaration, below imports,
// to make field static and accessible only within the file

@SpringBootApplication
@ConfigurationPropertiesScan
class NegotiatorBotApplication

fun main(args: Array<String>) {
    runApplication<NegotiatorBotApplication>(*args)
}
