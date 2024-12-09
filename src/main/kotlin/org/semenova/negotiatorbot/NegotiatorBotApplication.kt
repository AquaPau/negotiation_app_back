package org.semenova.negotiatorbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties
class NegotiatorBotApplication

fun main(args: Array<String>) {
    runApplication<NegotiatorBotApplication>(*args)
}
