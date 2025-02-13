package org.superapp.negotiatorbot.webclient.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.service.functiona.OpenAiUserService

@RestController
@Profile("dev")
@RequestMapping("/openai")
class OpenAIController(val openAiUserService: OpenAiUserService) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

    @GetMapping
    fun config(): ResponseEntity<String> {
        return ResponseEntity.ok("success")
    }

    @PostMapping("/{userId}")
    fun simplePrompt(@PathVariable userId: Long, @RequestParam prompt: String): ResponseEntity<String> {
        return ResponseEntity.ok(openAiUserService.startDialogWIthUserPrompt(userId, prompt))
    }

    @PostMapping("/file/{userId}")
    fun fileUpload(
        @PathVariable userId: Long,
        @RequestParam(required = true) fileName: String,
        @RequestBody(required = true) file: MultipartFile
    ): ResponseEntity<String> {
        coroutineScope.launch { openAiUserService.uploadFiles(userId, file.inputStream, fileName) }
        return ResponseEntity.status(201).body("your file is: ${fileName}")
    }

    @DeleteMapping("/file/{userId}")
    fun deleteFile(@PathVariable userId: Long): ResponseEntity<String> {
        coroutineScope.launch { openAiUserService.deleteFile(userId) }
        return ResponseEntity.ok("File is being deleted")
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(): ResponseEntity<String> {
        return ResponseEntity.notFound().build()
    }

}