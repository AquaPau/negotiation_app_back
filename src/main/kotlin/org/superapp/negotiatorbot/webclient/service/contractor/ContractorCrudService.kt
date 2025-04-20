package org.superapp.negotiatorbot.webclient.service.contractor

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.CounterpartyDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.dto.dadata.DadataRequest
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.entity.UserContractor
import org.superapp.negotiatorbot.webclient.entity.toDto
import org.superapp.negotiatorbot.webclient.enums.CompanyRegion
import org.superapp.negotiatorbot.webclient.exception.CompanyAlreadyExistsException
import org.superapp.negotiatorbot.webclient.exception.CompanyNotFoundException
import org.superapp.negotiatorbot.webclient.exception.ContractorNotFoundException
import org.superapp.negotiatorbot.webclient.exception.UserNotFoundException
import org.superapp.negotiatorbot.webclient.port.DadataPort
import org.superapp.negotiatorbot.webclient.repository.company.UserCompanyRepository
import org.superapp.negotiatorbot.webclient.repository.company.UserContractorRepository
import org.superapp.negotiatorbot.webclient.service.EnterpriseCrudService
import org.superapp.negotiatorbot.webclient.service.user.UserService

interface ContractorCrudService : EnterpriseCrudService<UserContractor> {
    fun get(companyId: Long, contractorId: Long): CompanyProfileDto
    fun getContractorsByCompanyId(companyId: Long): List<CounterpartyDto>

}

@Service
class ContractorCrudServiceImpl(
    private val userService: UserService,
    private val userContractorRepository: UserContractorRepository,
    private val userCompanyRepository: UserCompanyRepository,
    private val contractorDocumentService: ContractorDocumentService,
    private val dadataPort: DadataPort,
    @Value("\${dadata.token}") private val dadataToken: String
) : ContractorCrudService {

    @Transactional
    override fun create(enterpriseId: Long?, request: NewCompanyProfile): CompanyProfileDto {
        val user = userService.findById(request.userId)
            ?: throw UserNotFoundException(request.userId.toString())
        userCompanyRepository.findById(enterpriseId!!).orElseThrow { CompanyNotFoundException(enterpriseId) }
        return createContractor(user, enterpriseId, request)
    }

    override fun get(companyId: Long, contractorId: Long): CompanyProfileDto {
        val user = userService.getCurrentUser()
        return userContractorRepository.findByIdAndCompanyIdAndUser(contractorId, companyId, user)
            .orElseThrow { ContractorNotFoundException(contractorId) }.toDto()
    }

    @Transactional
    override fun delete(enterpriseId: Long) {
        val contractor = userContractorRepository.findById(enterpriseId)
            .orElseThrow { ContractorNotFoundException(enterpriseId) }
        contractorDocumentService.deleteDocuments(enterpriseId)
        userContractorRepository.delete(contractor)
    }

    override fun getContractorsByCompanyId(companyId: Long): List<CounterpartyDto> {
        val user = userCompanyRepository.findById(companyId)
            .orElseThrow { CompanyNotFoundException(companyId) }.user
        return userContractorRepository.findAllByCompanyIdAndUserOrderByIdAsc(companyId, user!!).map {
            CounterpartyDto(it.id!!, it.customUserGeneratedName)
        }
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
            ?: throw ContractorNotFoundException(null,
                "Данные об ОГРН контрагента " +
                        "${request.customUserGeneratedName} не найдены в ЕГРЮЛ/ЕГРИП"
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