package org.superapp.negotiatorbot.webclient.service

import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.CustomOAuth2User
import org.superapp.negotiatorbot.webclient.entity.UserProfile
import org.superapp.negotiatorbot.webclient.repository.UserRepository

@Service
class CustomOAuth2UserService : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val user = DefaultOAuth2UserService().loadUser(userRequest)

        // Получаем данные пользователя из OAuth2
        val userAttributes = user.attributes
        val email = userAttributes["email"] as String
        val name = userAttributes["name"] as String
        val userId = userAttributes["sub"] as String  // ID пользователя из Google

        val existingUser = userRepository.findById(userId).orElse(null)
        val userEntity = existingUser ?: UserProfile(
            id = userId,
            name = name,
            email = email,
            roles = "USER"  // Роль по умолчанию
        )
        userRepository.save(userEntity)

        return CustomOAuth2User(user, userAttributes)
    }
}
