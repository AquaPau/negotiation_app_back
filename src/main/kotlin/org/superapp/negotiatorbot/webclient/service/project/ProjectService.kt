package org.superapp.negotiatorbot.webclient.service.project

import org.superapp.negotiatorbot.webclient.dto.project.NewProjectDto
import org.superapp.negotiatorbot.webclient.dto.project.ProjectDto

interface ProjectService {

    fun getProjects(): List<ProjectDto>

    fun getProjectDtoById(id: Long): ProjectDto

    fun createProject(newProjectDto: NewProjectDto): ProjectDto

    fun deleteProject(id: Long)

}