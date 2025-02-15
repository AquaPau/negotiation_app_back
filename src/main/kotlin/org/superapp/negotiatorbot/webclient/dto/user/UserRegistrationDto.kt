package org.superapp.negotiatorbot.webclient.dto.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty


class UserRegistrationDto {
    var id: Long? = null
    var firstName: @NotEmpty String? = null
    var lastName: @NotEmpty String? = null
    var email: @NotEmpty(message = "Email should not be empty") @Email String? = null
    var password: @NotEmpty(message = "Password should be empty") String? = null
}