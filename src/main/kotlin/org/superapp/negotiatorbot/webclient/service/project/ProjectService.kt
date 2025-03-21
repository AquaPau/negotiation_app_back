package org.superapp.negotiatorbot.webclient.service.project

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.project.NewProjectDto
import org.superapp.negotiatorbot.webclient.dto.project.ProjectDto
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.entity.toDto
import org.superapp.negotiatorbot.webclient.repository.project.ProjectRepository
import org.superapp.negotiatorbot.webclient.service.user.UserService

interface ProjectService {

    fun getProjects(): List<ProjectDto>

    fun getProjectDtoById(id: Long): ProjectDto

    fun createProject(newProjectDto: NewProjectDto): ProjectDto

    fun deleteProject(id: Long)

}

@Service
class ProjectServceImpl(
    private val projectRepository: ProjectRepository,
    private val userService: UserService,
    private val projectDocumentService: ProjectDocumentService
) :
    ProjectService {
    override fun getProjects(): List<ProjectDto> {
        val user = userService.getCurrentUser() ?: throw NoSuchElementException()
        return projectRepository.findAllByUser(user).map { it.toDto() }
    }

    override fun getProjectDtoById(id: Long): ProjectDto {
        val user = userService.getCurrentUser() ?: throw NoSuchElementException()
        return projectRepository.findByIdAndUser(id, user).orElseThrow { NoSuchElementException() }.toDto()
    }

    @Transactional
    override fun createProject(newProjectDto: NewProjectDto): ProjectDto {
        val user = userService.findById(newProjectDto.userId)
            ?: throw NoSuchElementException("User ${newProjectDto.userId} is not found")
        val project = Project().apply {
            this.customUserGeneratedName = newProjectDto.customUserGeneratedName
            this.user = user
            this.userGeneratedPrompt = newProjectDto.userGeneratedPrompt
        }
        return projectRepository.save(project).toDto()
    }

    @Transactional
    override fun deleteProject(id: Long) {
        val project = projectRepository.findById(id).orElseThrow { NoSuchElementException() }
        projectDocumentService.deleteDocuments(projectId = id)
        projectRepository.delete(project)
    }

}