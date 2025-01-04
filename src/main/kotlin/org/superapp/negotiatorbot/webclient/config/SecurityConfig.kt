package org.superapp.negotiatorbot.webclient.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Value("\${spring.profiles.active}")
    private lateinit var activeProfile: String

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/", "/home").permitAll()
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .loginPage("/login")
                    .userInfoEndpoint { userInfo ->
                        userInfo.userService(oauth2UserService())
                    }
            }
            .logout { logout ->
                logout.permitAll()
            }

        if (isLocalhost()) {
            http.httpBasic(Customizer.withDefaults())  // Включаем Basic Authentication для локального профиля разработки
        }
        return http.build()
    }

    private fun isLocalhost(): Boolean {
        return activeProfile == "dev"
    }

    // Сервис для получения информации о пользователе
    private fun oauth2UserService(): OAuth2UserService<OAuth2UserRequest, OAuth2User> {
        return DefaultOAuth2UserService()
    }
}
