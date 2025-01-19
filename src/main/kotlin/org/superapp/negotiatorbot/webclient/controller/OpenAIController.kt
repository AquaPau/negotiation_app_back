package org.superapp.negotiatorbot.webclient.controller

import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.superapp.negotiatorbot.webclient.service.OpenAIService

@RestController
@Profile("dev")
@RequestMapping("/openai")
class OpenAIController(val openAIService: OpenAIService) {

    @PostMapping
    fun simplePrompt(@RequestParam prompt: String): ResponseEntity<String?> {
        val result = runBlocking { return@runBlocking ResponseEntity.ok(openAIService.userRoleStringPrompt(prompt)) }
        return result;
    }
}