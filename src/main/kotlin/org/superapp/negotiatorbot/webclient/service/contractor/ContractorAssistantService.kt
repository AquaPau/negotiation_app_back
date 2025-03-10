package org.superapp.negotiatorbot.webclient.service.contractor

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.UserContractor
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.repository.company.UserContractorRepository
import org.superapp.negotiatorbot.webclient.service.functionality.AssistantService
import org.superapp.negotiatorbot.webclient.service.functionality.openai.OpenAiAssistantService

@Service
class ContractorAssistantService(
    private val openAiAssistantService: OpenAiAssistantService,
    private val userContractorRepository: UserContractorRepository
) : AssistantService<UserContractor> {

    @Transactional
    override fun getAssistant(relatedId: Long): OpenAiAssistant {
        val userContractor = userContractorRepository.findById(relatedId).orElseThrow()
        return if (userContractor.assistantDbId != null) {
            openAiAssistantService.getAssistant(userContractor.assistantDbId!!)
        } else {
            val assistant = openAiAssistantService.createAssistant()
            userContractor.assistantDbId = assistant.id
            userContractorRepository.save(userContractor)
            return assistant
        }
    }
}