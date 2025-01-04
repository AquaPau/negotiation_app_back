package org.superapp.negotiatorbot.webclient.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/document")
class DocumentController {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int){

    }
}