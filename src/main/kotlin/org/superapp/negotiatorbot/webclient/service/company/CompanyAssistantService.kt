package org.superapp.negotiatorbot.webclient.service.company

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.entity.toDto
import org.superapp.negotiatorbot.webclient.repository.company.UserCompanyRepository
import org.superapp.negotiatorbot.webclient.repository.company.UserContractorRepository
import org.superapp.negotiatorbot.webclient.service.functiona.OpenAiAssistantService
import org.superapp.negotiatorbot.webclient.service.user.UserService

interface CompanyAssistantService {
    fun getCompanyAssistant(companyId: Long): OpenAiAssistant
    fun getContractorAssistant(contractorId: Long): OpenAiAssistant
}

@Service
class CompanyAssistantServiceImpl(
    private val openAiAssistantService: OpenAiAssistantService,
    private val userCompanyRepository: UserCompanyRepository,
    private val userContractorRepository: UserContractorRepository
) : CompanyAssistantService {

    @Transactional
    override fun getCompanyAssistant(companyId: Long): OpenAiAssistant {
        val userCompany = userCompanyRepository.findById(companyId).orElseThrow()
        return if (userCompany.assistantDbId != null) {
            openAiAssistantService.getAssistant(userCompany.assistantDbId!!)
        } else {
            val assistant = openAiAssistantService.createAssistant()
            userCompany.assistantDbId = assistant.id
            userCompanyRepository.save(userCompany)
            return assistant
        }
    }

    override fun getContractorAssistant(contractorId: Long): OpenAiAssistant {
        val userContractor = userContractorRepository.findById(contractorId).orElseThrow()
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