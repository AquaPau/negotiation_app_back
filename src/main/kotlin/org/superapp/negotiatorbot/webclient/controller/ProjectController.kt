package org.superapp.negotiatorbot.webclient.controller

import org.springframework.web.bind.annotation.*
import org.superapp.negotiatorbot.webclient.dto.project.NewProjectDto
import org.superapp.negotiatorbot.webclient.dto.project.ProjectDto
import org.superapp.negotiatorbot.webclient.service.project.ProjectService

@RestController
@RequestMapping("/api/project")
class ProjectController(private val projectService: ProjectService) {

    @GetMapping()
    fun getProjects(): List<ProjectDto> {
        return projectService.getProjects()
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: Long): ProjectDto {
        return projectService.getProjectDtoById(projectId)
    }

    @PostMapping()
    fun createNewProject(@RequestBody profile: NewProjectDto): ProjectDto {
        return projectService.createProject(profile)
    }

    @DeleteMapping("/{projectId}")
    fun deleteProject(@PathVariable projectId: Long) {
        projectService.deleteProject(projectId)
    }
}