package org.superapp.negotiatorbot.webclient.entity

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2User(
    private val delegate: OAuth2User,
    private val attributes: Map<String, Any>
) : OAuth2User {

    override fun getName(): String {
        return delegate.name
    }

    override fun getAttributes(): Map<String, Any> {
        return attributes
    }

    override fun getAuthorities(): Collection<SimpleGrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_USER"))  // Можно добавить роли пользователя
    }
}
