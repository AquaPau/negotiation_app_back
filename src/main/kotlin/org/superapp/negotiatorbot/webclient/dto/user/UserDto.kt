package org.superapp.negotiatorbot.webclient.dto.user

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.superapp.negotiatorbot.webclient.entity.User

data class UserDto(
    var id: Long,
    var name: String,
    var email: String,
    private var password: String
) : UserDetails {

    @JsonIgnore
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return emptyList()
    }

    @JsonIgnore
    override fun getUsername() = email

    @JsonIgnore
    override fun getPassword(): String {
        return password
    }

    /**
     * Если с аккаунтом что-то не так, то успешно залогиниться нельзя, поэтому везде ниже возвращаем true
     */
    @JsonIgnore
    override fun isEnabled(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return true
    }

    companion object {

        fun toUserDto(obj: User): UserDto {
            return UserDto(
                id = obj.id!!,
                name = obj.username!!,
                email = obj.email!!,
                password = obj.password!!
            )
        }
    }
}