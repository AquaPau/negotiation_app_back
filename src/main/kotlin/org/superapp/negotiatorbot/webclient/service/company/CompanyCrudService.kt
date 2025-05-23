package org.superapp.negotiatorbot.webclient.service.company

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.dto.dadata.DadataRequest
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.entity.UserCompany
import org.superapp.negotiatorbot.webclient.entity.toDto
import org.superapp.negotiatorbot.webclient.enums.CompanyRegion
import org.superapp.negotiatorbot.webclient.exception.CompanyAlreadyExistsException
import org.superapp.negotiatorbot.webclient.exception.CompanyNotFoundException
import org.superapp.negotiatorbot.webclient.exception.UserNotFoundException
import org.superapp.negotiatorbot.webclient.port.DadataPort
import org.superapp.negotiatorbot.webclient.repository.company.UserCompanyRepository
import org.superapp.negotiatorbot.webclient.repository.company.UserContractorRepository
import org.superapp.negotiatorbot.webclient.service.EnterpriseCrudService
import org.superapp.negotiatorbot.webclient.service.user.UserService

interface CompanyCrudService : EnterpriseCrudService<UserCompany> {

    fun getAll(): List<CompanyProfileDto>

    fun get(companyId: Long): CompanyProfileDto


}

@Service
class CompanyCrudServiceImpl(
    private val userService: UserService,
    private val userCompanyRepository: UserCompanyRepository,
    private val userContractorRepository: UserContractorRepository,
    private val companyDocumentService: CompanyDocumentService,
    private val dadataPort: DadataPort,
    @Value("\${dadata.token}") private val dadataToken: String
) : CompanyCrudService {

    @Transactional
    override fun create(enterpriseId: Long?, request: NewCompanyProfile): CompanyProfileDto {
        val user = userService.findById(request.userId)
            ?: throw UserNotFoundException(request.userId.toString())
        return createOwnCompany(user, request)
    }


    override fun getAll(): List<CompanyProfileDto> {
        val user = userService.getCurrentUser()
        return userCompanyRepository.findAllByUserOrderByIdAsc(user).map {
            it.toDto()
        }
    }

    override fun get(companyId: Long): CompanyProfileDto {
        val user = userService.getCurrentUser()
        return userCompanyRepository.findByUserAndId(user, companyId)
            .orElseThrow { CompanyNotFoundException(companyId) }
            .toDto()
    }

    @Transactional
    override fun delete(enterpriseId: Long) {
        val company = userCompanyRepository.findById(enterpriseId)
            .orElseThrow { CompanyNotFoundException(enterpriseId) }
        val contractors = userContractorRepository.findAllByCompanyId(enterpriseId)

        companyDocumentService.deleteCompanyDocuments(enterpriseId)

        userContractorRepository.deleteAll(contractors)
        userCompanyRepository.delete(company)
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
            ?: throw CompanyNotFoundException(
                null,
                "Данные об ОГРН компании " +
                        "${request.customUserGeneratedName} не найдены в ЕГРЮЛ/ЕГРИП"
            )
        userCompany.inn = companyData.data.inn
        userCompany.fullName = companyData.value
        userCompany.address = companyData.data.address.value
        userCompany.managerName = companyData.data.management?.name
        userCompany.managerTitle = companyData.data.management?.post

        userCompanyRepository.save(userCompany)
        return userCompany.toDto()
    }
}
