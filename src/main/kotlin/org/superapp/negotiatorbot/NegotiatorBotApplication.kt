package org.superapp.negotiatorbot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

@SpringBootApplication
@EnableConfigurationProperties
class NegotiatorBotApplication

fun main(args: Array<String>) {
    SpringApplication.run(NegotiatorBotApplication::class.java, *args)
}
