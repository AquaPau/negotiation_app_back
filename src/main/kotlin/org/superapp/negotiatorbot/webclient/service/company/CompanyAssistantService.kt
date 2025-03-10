package org.superapp.negotiatorbot.webclient.service.company

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.UserCompany
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.repository.company.UserCompanyRepository
import org.superapp.negotiatorbot.webclient.service.functionality.AssistantService
import org.superapp.negotiatorbot.webclient.service.functionality.openai.OpenAiAssistantService

@Service
class CompanyAssistantServiceImpl(
    private val openAiAssistantService: OpenAiAssistantService,
    private val userCompanyRepository: UserCompanyRepository
) : AssistantService<UserCompany> {

    @Transactional
    override fun getAssistant(relatedId: Long): OpenAiAssistant {
        val userCompany = userCompanyRepository.findById(relatedId).orElseThrow()
        return if (userCompany.assistantDbId != null) {
            openAiAssistantService.getAssistant(userCompany.assistantDbId!!)
        } else {
            val assistant = openAiAssistantService.createAssistant()
            userCompany.assistantDbId = assistant.id
            userCompanyRepository.save(userCompany)
            return assistant
        }
    }


}