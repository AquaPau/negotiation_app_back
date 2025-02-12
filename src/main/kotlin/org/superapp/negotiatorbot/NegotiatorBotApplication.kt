package org.superapp.negotiatorbot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableConfigurationProperties
@EnableFeignClients
class NegotiatorBotApplication

fun main(args: Array<String>) {
    SpringApplication.run(NegotiatorBotApplication::class.java, *args)
}
