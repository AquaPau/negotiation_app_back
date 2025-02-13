package org.superapp.negotiatorbot.webclient.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.service.serversidefile.ServerSideFileService
import org.superapp.negotiatorbot.webclient.service.user.UserService

@RestController
@RequestMapping("/file")
class ServerSideFileController(
    private val userService: UserService,
    private val serverSideFileService: ServerSideFileService
) {

    @PostMapping("/{userId}")
    fun fileUpload(
        @PathVariable userId: Long,
        @RequestParam(required = true) nameWithExtension: String,
        @RequestBody(required = true) file: MultipartFile
    ): ResponseEntity<String> {
        val user = userService.findById(userId) ?: throw NoSuchElementException("User is not found")
        val serverSideFile = serverSideFileService.save(
            user,
            BusinessType.USER,
            nameWithExtension,
            file
        ) //todo catch constraint violation exception (user cannot have not unique file names for this user)
        return ResponseEntity.status(201).body("your file is: ${serverSideFile.path}")
    }


    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(): ResponseEntity<String> {
        return ResponseEntity.notFound().build()
    }

}