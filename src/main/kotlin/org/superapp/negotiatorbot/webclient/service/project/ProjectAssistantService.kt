package org.superapp.negotiatorbot.webclient.service.project

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.repository.project.ProjectRepository
import org.superapp.negotiatorbot.webclient.service.functionality.AssistantService
import org.superapp.negotiatorbot.webclient.service.functionality.openai.OpenAiAssistantService

@Service
class ProjectAssistantService(
    private val openAiAssistantService: OpenAiAssistantService,
    private val projectRepository: ProjectRepository
) : AssistantService<Project> {
    @Transactional
    override fun getAssistant(relatedId: Long): OpenAiAssistant {
        val project = projectRepository.findById(relatedId).orElseThrow()
        return if (project.assistantDbId != null) {
            openAiAssistantService.getAssistant(project.assistantDbId!!)
        } else {
            val assistant = openAiAssistantService.createAssistant()
            project.assistantDbId = assistant.id
            projectRepository.save(project)
            return assistant
        }
    }
}