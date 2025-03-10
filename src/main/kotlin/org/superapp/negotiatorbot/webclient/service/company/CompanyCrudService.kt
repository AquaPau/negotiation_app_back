package org.superapp.negotiatorbot.webclient.service.company

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.CounterpartyDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.dto.dadata.DadataRequest
import org.superapp.negotiatorbot.webclient.entity.*
import org.superapp.negotiatorbot.webclient.enum.CompanyRegion
import org.superapp.negotiatorbot.webclient.exception.CompanyAlreadyExistsException
import org.superapp.negotiatorbot.webclient.port.DadataPort
import org.superapp.negotiatorbot.webclient.repository.company.UserCompanyRepository
import org.superapp.negotiatorbot.webclient.repository.company.UserContractorRepository
import org.superapp.negotiatorbot.webclient.service.user.UserService

interface CompanyService {

    fun createCompany(companyId: Long? = null, request: NewCompanyProfile, isOwn: Boolean): CompanyProfileDto

    fun getCompanies(): List<CompanyProfileDto>
    fun getCompanyDtoById(companyId: Long): CompanyProfileDto

    fun getContractor(companyId: Long, contractorId: Long): CompanyProfileDto

    fun deleteCompany(companyId: Long)

    fun deleteContractor(contractorId: Long)

    fun getCounterparties(companyId: Long): List<CounterpartyDto>


}

@Service
class CompanyServiceImpl(
    private val userService: UserService,
    private val userCompanyRepository: UserCompanyRepository,
    private val userContractorRepository: UserContractorRepository,
    private val companyDocumentService: CompanyDocumentService,
    private val dadataPort: DadataPort,
    @Value("\${dadata.token}") private val dadataToken: String
) : CompanyService {

    @Transactional
    override fun createCompany(companyId: Long?, request: NewCompanyProfile, isOwn: Boolean): CompanyProfileDto {
        val user = userService.findById(request.userId)
            ?: throw NoSuchElementException("User ${request.userId} is not found")
        return if (isOwn) {
            createOwnCompany(user, request)
        } else {
            userCompanyRepository.findById(companyId!!).orElseThrow { NoSuchElementException("Company doesn't exist") }
            createContractor(user, companyId, request)
        }
    }

    override fun getContractor(companyId: Long, contractorId: Long): CompanyProfileDto {
        val user = userService.getCurrentUser() ?: throw NoSuchElementException("User not found")
        return userContractorRepository.findByIdAndCompanyIdAndUser(contractorId, companyId, user)
            .orElseThrow { NoSuchElementException("Contractor not found") }.toDto()
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

    override fun deleteCompany(companyId: Long) {
        val company = userCompanyRepository.findById(companyId)
            .orElseThrow { NoSuchElementException("User company is not found") }
        val contractors = userContractorRepository.findAllByCompanyId(companyId)

        companyDocumentService.deleteCompanyDocuments(companyId)

        userContractorRepository.deleteAll(contractors)
        userCompanyRepository.delete(company)
    }

    override fun deleteContractor(contractorId: Long) {
        val contractor = userContractorRepository.findById(contractorId).orElseThrow()
        companyDocumentService.deleteContractorDocuments(contractorId)
        userContractorRepository.delete(contractor)
    }

    override fun getCounterparties(companyId: Long): List<CounterpartyDto> {
        val user = userCompanyRepository.findById(companyId)
            .orElseThrow { NoSuchElementException("Company is not found") }.user
        return userContractorRepository.findAllByCompanyIdAndUser(companyId, user!!).map {
            CounterpartyDto(it.id!!, it.customUserGeneratedName)
        }
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
