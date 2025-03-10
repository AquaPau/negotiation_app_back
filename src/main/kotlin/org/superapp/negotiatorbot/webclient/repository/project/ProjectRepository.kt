package org.superapp.negotiatorbot.webclient.repository.project

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.superapp.negotiatorbot.webclient.entity.Project

@Repository
interface ProjectRepository: JpaRepository<Project, Long>