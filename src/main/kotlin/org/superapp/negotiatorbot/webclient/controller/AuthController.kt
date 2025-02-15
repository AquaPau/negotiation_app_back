package org.superapp.negotiatorbot.webclient.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.superapp.negotiatorbot.webclient.dto.user.LoginUser
import org.superapp.negotiatorbot.webclient.dto.user.UserRegistrationDto
import org.superapp.negotiatorbot.webclient.service.user.UserService

@RestController
@RequestMapping("/api/auth")
class AuthController(private val userService: UserService) {

    @PostMapping("/register")
    fun register(@RequestBody user: UserRegistrationDto) {
        userService.saveUser(user)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody requestParameters: LoginUser,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Long? {

        return userService.login(requestParameters.email, requestParameters.password, request, response)
    }

    @PostMapping("/logout")
    fun logout() {
        userService.logout()
    }
}