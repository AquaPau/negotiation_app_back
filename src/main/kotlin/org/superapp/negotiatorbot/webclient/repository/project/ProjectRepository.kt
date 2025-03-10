package org.superapp.negotiatorbot.webclient.repository.project

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.entity.User
import java.util.*

@Repository
interface ProjectRepository : JpaRepository<Project, Long> {

    fun findAllByUser(user: User): List<Project>

    fun findByIdAndUser(id: Long, user: User): Optional<Project>
}