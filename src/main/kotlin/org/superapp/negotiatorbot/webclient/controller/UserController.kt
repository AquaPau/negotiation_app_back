package org.superapp.negotiatorbot.webclient.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    @GetMapping("/me")
    fun getUserData(){

    }

    @GetMapping("/me/document")
    fun getUserDocuments() {

    }


}