package org.superapp.negotiatorbot.webclient.controller

import kotlinx.coroutines.*
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.service.OpenAIService

@RestController
@Profile("dev")
@RequestMapping("/openai")
class OpenAIController(val openAIService: OpenAIService) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

    @GetMapping
    fun config(): ResponseEntity<String> {
        return ResponseEntity.ok("success")
    }

    @PostMapping("/{userId}")
    fun simplePrompt(@PathVariable userId: Long, @RequestParam prompt: String): ResponseEntity<String> {
        return ResponseEntity.ok(openAIService.userPrompt(userId, prompt))
    }

    @PostMapping("/file/{userId}")
    fun fileUpload(@PathVariable userId: Long,@RequestParam(required = true) fileName: String, @RequestBody(required = true) file: MultipartFile): ResponseEntity<String> {
        coroutineScope.launch { openAIService.uploadFile(userId, file.inputStream, fileName) }
        return ResponseEntity.status(201).body("your file is: ${fileName}")
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(): ResponseEntity<String> {
        return ResponseEntity.notFound().build()
    }

}