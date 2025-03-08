package org.superapp.negotiatorbot.webclient.service.company

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.CounterpartyDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.dto.dadata.DadataRequest
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.*
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.enum.CompanyRegion
import org.superapp.negotiatorbot.webclient.exception.CompanyAlreadyExistsException
import org.superapp.negotiatorbot.webclient.port.DadataPort
import org.superapp.negotiatorbot.webclient.repository.UserCompanyRepository
import org.superapp.negotiatorbot.webclient.repository.UserContractorRepository
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService
import org.superapp.negotiatorbot.webclient.service.functiona.OpenAiAssistantService
import org.superapp.negotiatorbot.webclient.service.user.UserService

interface CompanyService {

    fun createCompany(companyId: Long? = null, request: NewCompanyProfile, isOwn: Boolean): CompanyProfileDto

    fun getCompanies(): List<CompanyProfileDto>
    fun getCompanyDtoById(companyId: Long): CompanyProfileDto

    fun deleteCompany(companyId: Long)

    fun getContractorAssistant(companyId: Long, contractorId: Long): CompanyProfileDto

    fun deleteContractor(contractorId: Long)

    fun uploadDocuments(
        files: List<RawDocumentAndMetatype>,
        relatedId: Long,
        type: BusinessType
    )

    fun getCounterparties(companyId: Long): List<CounterpartyDto>

    fun getDocuments(companyId: Long, type: BusinessType): List<DocumentMetadataDto>

    fun getCompanyAssistant(companyId: Long): OpenAiAssistant
    fun getContractorAssistant(contractorId: Long): OpenAiAssistant
}

@Service
class CompanyServiceImpl(
    private val userService: UserService,
    private val userCompanyRepository: UserCompanyRepository,
    private val userContractorRepository: UserContractorRepository,
    private val openAiAssistantService: OpenAiAssistantService,
    private val documentService: DocumentService,
    private val dadataPort: DadataPort,
    @Value("\${dadata.token}") private val dadataToken: String
) : CompanyService {

    @Transactional
    override fun getCompanyAssistant(companyId: Long): OpenAiAssistant {
        val userCompany = userCompanyRepository.findById(companyId).orElseThrow()
       return  if(userCompany.assistantDbId != null) {
            openAiAssistantService.getAssistant(userCompany.assistantDbId!!)
        } else{
            val assistant = openAiAssistantService.createAssistant()
           userCompany.assistantDbId = assistant.id
           userCompanyRepository.save(userCompany)
           return assistant
        }
    }

    override fun getContractorAssistant(contractorId: Long): OpenAiAssistant {
        val userContractor = userContractorRepository.findById(contractorId).orElseThrow()
        return  if(userContractor.assistantDbId != null) {
            openAiAssistantService.getAssistant(userContractor.assistantDbId!!)
        } else{
            val assistant = openAiAssistantService.createAssistant()
            userContractor.assistantDbId = assistant.id
            userContractorRepository.save(userContractor)
            return assistant
        }
    }

    @Transactional
    override fun createCompany(companyId: Long?, request: NewCompanyProfile, isOwn: Boolean): CompanyProfileDto {
        val user = userService.findById(request.userId)
            ?: throw NoSuchElementException("User ${request.userId} is not found")
        if (isOwn) {
            return createOwnCompany(user, request)
        } else {
            userCompanyRepository.findById(companyId!!).orElseThrow { NoSuchElementException("Company doesn't exist") }
            return createContractor(user, companyId, request)
        }
    }

    override fun getCompanies(): List<CompanyProfileDto> {
        val user = userService.getCurrentUser() ?: throw NoSuchElementException("User not found")
        return userCompanyRepository.findAllByUser(user).map {
            it.toDto()
        }
    }

    override fun getCompanyDtoById(companyId: Long): CompanyProfileDto {
        val user = userService.getCurrentUser() ?: throw NoSuchElementException("User not found")
        return userCompanyRepository.findByUserAndId(user, companyId)
            .orElseThrow { NoSuchElementException("Company not found") }
            .toDto()
    }

    override fun getContractorAssistant(companyId: Long, contractorId: Long): CompanyProfileDto {
        val user = userService.getCurrentUser() ?: throw NoSuchElementException("User not found")
        return userContractorRepository.findByIdAndCompanyIdAndUser(contractorId, companyId, user)
            .orElseThrow { NoSuchElementException("Contractor not found") }.toDto()
    }

    override fun deleteCompany(companyId: Long) {
        val company = userCompanyRepository.findById(companyId)
            .orElseThrow { NoSuchElementException("User company is not found") }
        val contractors = userContractorRepository.findAllByCompanyId(companyId)

        documentService.deleteCompanyDocuments(company.id!!)
        contractors.forEach { documentService.deleteContractorDocuments(it.id!!) }

        userContractorRepository.deleteAll(contractors)
        userCompanyRepository.delete(company)
    }

    override fun uploadDocuments(
        files: List<RawDocumentAndMetatype>,
        relatedId: Long,
        type: BusinessType
    ) {
        val user = when (type) {
            BusinessType.USER -> userCompanyRepository.findById(relatedId)
                .orElseThrow { NoSuchElementException("Own company is not found") }.user

            BusinessType.PARTNER -> userContractorRepository.findById(relatedId)
                .orElseThrow { NoSuchElementException("Counterparty is not found") }.user

            BusinessType.PROJECT -> TODO()
        }

        documentService.batchSave(
            user!!.id!!,
            type,
            relatedId,
            files
        )

    }

    override fun deleteContractor(contractorId: Long) {
        val contractor = userContractorRepository.findById(contractorId).orElseThrow()
        documentService.deleteContractorDocuments(contractorId)
        userContractorRepository.delete(contractor)
    }

    override fun getCounterparties(companyId: Long): List<CounterpartyDto> {
        val user = userCompanyRepository.findById(companyId)
            .orElseThrow { NoSuchElementException("Company is not found") }.user
        return userContractorRepository.findAllByCompanyIdAndUser(companyId, user!!).map {
            CounterpartyDto(it.id!!, it.customUserGeneratedName)
        }
    }

    override fun getDocuments(companyId: Long, type: BusinessType): List<DocumentMetadataDto> {
        val user = userCompanyRepository.findById(companyId)
            .orElseThrow { NoSuchElementException("Company is not found") }.user
        return documentService.getDocumentList(user!!.id!!, companyId)
    }

    private fun createOwnCompany(
        user: User,
        request: NewCompanyProfile
    ): CompanyProfileDto {
        val userCompany = UserCompany()
        userCompany.user = user
        userCompany.customUserGeneratedName = request.customUserGeneratedName
        userCompany.residence = CompanyRegion.RU
        userCompany.ogrn = request.ogrn.toString()
        userCompanyRepository.findByOgrn(request.ogrn.toString())
            .ifPresent { throw CompanyAlreadyExistsException(request.ogrn) }

        val companyData = dadataPort.findCompanyByInn(
            DadataRequest(query = request.ogrn.toString()), token = dadataToken
        ).suggestions.firstOrNull()
            ?: throw NoSuchElementException(
                "Data about company " +
                        "${request.customUserGeneratedName} is not found in dadata"
            )
        userCompany.inn = companyData.data.inn
        userCompany.fullName = companyData.value
        userCompany.address = companyData.data.address.value
        userCompany.managerName = companyData.data.management?.name
        userCompany.managerTitle = companyData.data.management?.post

        userCompanyRepository.save(userCompany)
        return userCompany.toDto()
    }

    private fun createContractor(
        user: User,
        companyId: Long,
        request: NewCompanyProfile
    ): CompanyProfileDto {
        val contractorCompany = UserContractor(
            user = user,
            companyId = companyId,
            customUserGeneratedName = request.customUserGeneratedName,
            residence = CompanyRegion.RU
        )
        userCompanyRepository.findByOgrn(request.ogrn.toString())
            .ifPresent { throw CompanyAlreadyExistsException(request.ogrn) }

        val companyData = dadataPort.findCompanyByInn(
            DadataRequest(query = request.ogrn.toString()), token = dadataToken
        ).suggestions.firstOrNull()
            ?: throw NoSuchElementException(
                "Data about counterparty" +
                        " ${request.customUserGeneratedName} is not found in dadata"
            )

        contractorCompany.inn = companyData.data.inn
        contractorCompany.ogrn = companyData.data.ogrn
        contractorCompany.fullName = companyData.value
        contractorCompany.address = companyData.data.address.value
        contractorCompany.managerName = companyData.data.management?.name
        contractorCompany.managerTitle = companyData.data.management?.post
        userContractorRepository.save(contractorCompany)
        return contractorCompany.toDto()
    }
}
