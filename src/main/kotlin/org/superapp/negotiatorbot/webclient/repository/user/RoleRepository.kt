package org.superapp.negotiatorbot.webclient.repository.user

import org.springframework.data.jpa.repository.JpaRepository
import org.superapp.negotiatorbot.webclient.entity.Role
import org.superapp.negotiatorbot.webclient.enums.Roles
import java.util.Optional


interface RoleRepository : JpaRepository<Role, Long?> {
    fun findByName(name: Roles): Optional<Role>
}