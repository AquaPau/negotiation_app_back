package org.superapp.negotiatorbot.webclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.superapp.negotiatorbot.webclient.entity.Role


interface RoleRepository : JpaRepository<Role, Long?> {
    fun findByName(name: String?): Role?
}