package org.superapp.negotiatorbot.webclient.controller

import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.superapp.negotiatorbot.webclient.dto.user.UserDto
import org.superapp.negotiatorbot.webclient.service.UserService


@Controller
class AuthController @Autowired constructor(
    private val userService: UserService
) {

    @GetMapping("/register")
    fun showRegistrationForm(model: Model): String {
        // create model object to store form data
        val user = UserDto()
        model.addAttribute("user", user)
        return "register"
    }

    // handler method to handle user registration form submit request
    @PostMapping("/register/save")
    fun registration(
        @ModelAttribute("user") userDto: @Valid UserDto?,
        result: BindingResult,
        model: Model
    ): String {
        val existingUser = userDto!!.email?.let { userService.findUserByEmail(it) }
        if (existingUser?.email != null && existingUser.email!!.isNotEmpty()) {
            result.rejectValue(
                "email", "404",
                "There is already an account registered with the same email"
            )
        }
        if (result.hasErrors()) {
            model.addAttribute("user", userDto)
            return "/register"
        }
        userService.saveUser(userDto)
        return "redirect:/register?success"
    }

    @GetMapping("/login")
    fun login(): String {
        return "login"
    }
}
