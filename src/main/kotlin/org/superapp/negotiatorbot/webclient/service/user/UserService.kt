package org.superapp.negotiatorbot.webclient.service.user

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.user.UserDto
import org.superapp.negotiatorbot.webclient.dto.user.UserRegistrationDto
import org.superapp.negotiatorbot.webclient.entity.Role
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.repository.RoleRepository
import org.superapp.negotiatorbot.webclient.repository.UserRepository


interface UserService {
    fun saveUser(userRegistrationDto: UserRegistrationDto)
    fun findUserByEmail(email: String): User?
    fun findById(userId: Long): User?
    fun findAllUsers(): List<UserRegistrationDto?>?

    fun login(
        login: String,
        password: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Long?

    fun logout()

    fun getCurrentUserDto(): UserDto?

    fun getCurrentUser(): User?
}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
) : UserService {


    override fun saveUser(userRegistrationDto: UserRegistrationDto) {
        val user = User()
        user.name = userRegistrationDto.firstName + " " + userRegistrationDto.lastName
        user.email = userRegistrationDto.email
        user.password = passwordEncoder.encode(userRegistrationDto.password)

        var role = roleRepository.findByName("ROLE_ADMIN");
        if (role == null) {
            role = checkRoleExist();
        }
        user.roles = listOf(role)
        userRepository.save(user);
    }

    override fun findUserByEmail(email: String): User? {
        return userRepository.findByEmail(email).orElseGet { null }
    }

    override fun findById(userId: Long): User? {
        return userRepository.findById(userId).orElseGet { null }
    }

    override fun findAllUsers(): List<UserRegistrationDto> {
        val users = userRepository.findAll();
        return users.map { mapToUserDto(it) }
    }

    override fun login(
        login: String,
        password: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Long? {
        return try {
            val token = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(login, password, emptyList())
            )
            SecurityContextHolder.getContext().authentication = token
            return userRepository.findByEmail(login).orElseThrow { NoSuchElementException("User was not found") }.id
        } catch (e: AuthenticationException) {
            logout()
            null
        }
    }

    override fun logout() {
        SecurityContextHolder.getContext().authentication = null
        SecurityContextHolder.clearContext()
    }

    override fun getCurrentUserDto(): UserDto? {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication == null || authentication.principal !is User) {
            null
        } else {
            authentication.principal as UserDto
        }
    }

    override fun getCurrentUser(): User? {
        val userId = getCurrentUserDto()?.id
        return userId?.let { userRepository.findById(userId) }?.orElseGet { null }
    }

    private fun mapToUserDto(user: User): UserRegistrationDto {
        val userRegistrationDto = UserRegistrationDto()
        val name = user.name?.split(" ") ?: emptyList()
        userRegistrationDto.firstName = name[0]
        userRegistrationDto.lastName = name[1]
        userRegistrationDto.email = user.email
        return userRegistrationDto;
    }

    private fun checkRoleExist(): Role {
        val role = Role();
        role.name = "ROLE_ADMIN";
        return roleRepository.save(role);
    }
}