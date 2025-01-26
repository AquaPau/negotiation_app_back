package org.superapp.negotiatorbot.webclient.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.superapp.negotiatorbot.webclient.repository.UserRepository
import org.superapp.negotiatorbot.webclient.service.CustomUserDetailsService


@Configuration
@EnableWebSecurity
class SecurityConfig(private val userRepository: UserRepository) {

    @Throws(Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth
            .userDetailsService(CustomUserDetailsService(userRepository))
            .passwordEncoder(passwordEncoder())
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(5)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers(HttpMethod.POST, "/openai/**").permitAll()
                    .requestMatchers(
                        "/login", "logout", "/register/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/swagger-ui.html")
                    .permitAll()
            }.logout { logout ->
                logout
                    .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
                    .permitAll()
            }

        return http.build()
    }
}
