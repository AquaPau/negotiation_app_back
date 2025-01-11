package org.superapp.negotiatorbot.webclient.controller

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
    suspend fun simplePrompt(@RequestParam prompt: String): ResponseEntity<String> {
        val result = openAIService.userRoleStringPrompt(prompt)
        return ResponseEntity.ok(result)
    }
}