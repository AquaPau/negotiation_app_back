package org.superapp.negotiatorbot.webclient.service.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.user.UserDto
import org.superapp.negotiatorbot.webclient.entity.Role
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.repository.RoleRepository
import org.superapp.negotiatorbot.webclient.repository.UserRepository


interface UserService {
    fun saveUser(userDto: UserDto)
    fun findUserByEmail(email: String): User?
    fun findById(userId: Long): User?
    fun findAllUsers(): List<UserDto?>?
}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {


    override fun saveUser(userDto: UserDto) {
        val user = User()
        user.name = userDto.firstName + " " + userDto.lastName
        user.email = userDto.email
        user.password = passwordEncoder.encode(userDto.password)

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

    override fun findAllUsers(): List<UserDto> {
        val users = userRepository.findAll();
        return users.map { mapToUserDto(it) }
    }

    private fun mapToUserDto(user: User): UserDto {
        val userDto = UserDto()
        val name = user.name?.split(" ") ?: emptyList()
        userDto.firstName = name[0]
        userDto.lastName = name[1]
        userDto.email = user.email
        return userDto;
    }

    private fun checkRoleExist(): Role {
        val role = Role();
        role.name = "ROLE_ADMIN";
        return roleRepository.save(role);
    }
}