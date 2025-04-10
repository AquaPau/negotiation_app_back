package org.superapp.negotiatorbot.webclient.service.project

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.project.NewProjectDto
import org.superapp.negotiatorbot.webclient.dto.project.ProjectDto
import org.superapp.negotiatorbot.webclient.dto.project.ProjectSlimDto
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.entity.toDto
import org.superapp.negotiatorbot.webclient.enums.TaskType
import org.superapp.negotiatorbot.webclient.exception.ProjectNotFoundException
import org.superapp.negotiatorbot.webclient.exception.TaskNotFoundException
import org.superapp.negotiatorbot.webclient.exception.UserNotFoundException
import org.superapp.negotiatorbot.webclient.repository.project.ProjectRepository
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskRecordService
import org.superapp.negotiatorbot.webclient.service.user.UserService

interface ProjectService {

    fun getProjects(): List<ProjectSlimDto>

    fun getProjectDtoById(id: Long): ProjectDto

    fun getProjectById(id: Long): Project

    fun createProject(newProjectDto: NewProjectDto): ProjectDto

    fun deleteProject(id: Long)

}

@Service
class ProjectServceImpl(
    private val projectRepository: ProjectRepository,
    private val userService: UserService,
    private val projectDocumentService: ProjectDocumentService,
    private val taskRecordService: TaskRecordService
) : ProjectService {
    override fun getProjects(): List<ProjectSlimDto> {
        val user = userService.getCurrentUser()
        return projectRepository.findAllByUser(user).map { it.toDto() }
    }

    override fun getProjectDtoById(id: Long): ProjectDto {
        val project = getProjectById(id)
        val tasks = try {
            taskRecordService.getAllByTypeAndReference(TaskType.PROJECT_RESOLUTION, id)
        } catch (e: TaskNotFoundException) {
            null
        }
        return project.toDto(tasks)
    }

    override fun getProjectById(id: Long): Project {
        val user = userService.getCurrentUser()
        return projectRepository.findByIdAndUser(id, user).orElseThrow { ProjectNotFoundException(id) }
    }

    @Transactional
    override fun createProject(newProjectDto: NewProjectDto): ProjectDto {
        val user = userService.findById(newProjectDto.userId)
            ?: throw UserNotFoundException(newProjectDto.userId.toString())
        val project = Project().apply {
            this.customUserGeneratedName = newProjectDto.customUserGeneratedName
            this.user = user
            this.userGeneratedPrompt = newProjectDto.userGeneratedPrompt
        }
        return projectRepository.save(project).toDto(null)
    }

    @Transactional
    override fun deleteProject(id: Long) {
        val project = projectRepository.findById(id).orElseThrow { ProjectNotFoundException(id) }
        projectDocumentService.deleteDocuments(projectId = id)
        projectRepository.delete(project)
    }

}