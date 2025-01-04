package org.superapp.negotiatorbot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class NegotiatorBotApplication

fun main(args: Array<String>) {
    SpringApplication.run(NegotiatorBotApplication::class.java, *args)
}
