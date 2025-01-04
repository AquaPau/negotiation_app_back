package org.superapp.negotiatorbot.webclient.dto.user

data class UserDto(
    val id: Int,
    val name: String,
    val surname: String,
    val login: String,
    val phone: String
)