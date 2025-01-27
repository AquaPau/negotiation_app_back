package org.superapp.negotiatorbot.webclient.controller

import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.superapp.negotiatorbot.webclient.service.OpenAIService

@RestController
@Profile("dev")
@RequestMapping("/openai")
class OpenAIController(val openAIService: OpenAIService) {

    @GetMapping
    fun config(): ResponseEntity<String> {
        return ResponseEntity.ok("success")
    }

    @PostMapping("/{userId}")
    fun simplePrompt(@PathVariable userId: Long, @RequestParam prompt: String): ResponseEntity<String> {
        return ResponseEntity.ok(openAIService.userPrompt(userId, prompt))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(): ResponseEntity<String> {
        return ResponseEntity.notFound().build()
    }

}