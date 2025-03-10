package org.superapp.negotiatorbot.webclient.service.user

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.repository.user.UserRepository
import java.util.*
import java.util.stream.Collectors


@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(usernameOrEmail: String?): UserDetails {
        val user = userRepository.findByEmail(usernameOrEmail!!)
            .orElseThrow { UsernameNotFoundException(usernameOrEmail) }
        return User(user.email, user.password,
            user.roles.stream()
                .map { role -> SimpleGrantedAuthority(role.name?.name) }
                .collect(Collectors.toList()))
    }


}