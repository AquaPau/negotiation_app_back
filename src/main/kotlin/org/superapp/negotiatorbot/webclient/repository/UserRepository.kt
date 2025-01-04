package org.superapp.negotiatorbot.webclient.repository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.superapp.negotiatorbot.webclient.entity.User

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findByEmail(email: String): User?
}
