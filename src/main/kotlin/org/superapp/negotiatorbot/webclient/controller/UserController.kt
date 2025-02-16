package org.superapp.negotiatorbot.webclient.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.superapp.negotiatorbot.webclient.dto.user.UserDto
import org.superapp.negotiatorbot.webclient.service.user.UserService

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {

    @GetMapping("/current-user")
    fun getUserData(): UserDto? {
        return userService.getCurrentUserDto()
    }


}