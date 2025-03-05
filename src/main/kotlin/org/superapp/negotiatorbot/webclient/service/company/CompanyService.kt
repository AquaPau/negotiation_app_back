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
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.entity.UserCompany
import org.superapp.negotiatorbot.webclient.entity.UserContractor
import org.superapp.negotiatorbot.webclient.entity.toDto
import org.superapp.negotiatorbot.webclient.enum.CompanyRegion
import org.superapp.negotiatorbot.webclient.exception.CompanyAlreadyExistsException
import org.superapp.negotiatorbot.webclient.port.DadataPort
import org.superapp.negotiatorbot.webclient.repository.UserCompanyRepository
import org.superapp.negotiatorbot.webclient.repository.UserContractorRepository
import org.superapp.negotiatorbot.webclient.service.metadatafile.DocumentService
import org.superapp.negotiatorbot.webclient.service.user.UserService

interface CompanyService {

    fun createCompany(companyId: Long? = null, request: NewCompanyProfile, isOwn: Boolean): CompanyProfileDto


    fun getCompanies(): List<CompanyProfileDto>
    fun getCompanyById(companyId: Long): CompanyProfileDto

    fun deleteCompany(companyId: Long)

    fun getContractor(companyId: Long, contractorId: Long): CompanyProfileDto

    fun deleteContractor(contractorId: Long)

    fun uploadDocuments(
        files: List<RawDocumentAndMetatype>,
        companyId: Long,
        contractorId: Long? = null,
        type: BusinessType
    )

    fun getCounterparties(companyId: Long): List<CounterpartyDto>

    fun getDocuments(companyId: Long, type: BusinessType): List<DocumentMetadataDto>
}

@Service
class CompanyServiceImpl(
    private val userService: UserService,
    private val userCompanyRepository: UserCompanyRepository,
    private val userContractorRepository: UserContractorRepository,
    private val documentService: DocumentService,
    private val dadataPort: DadataPort,
    @Value("\${dadata.token}") private val dadataToken: String
) : CompanyService {
    override fun createCompany(companyId: Long?, request: NewCompanyProfile, isOwn: Boolean): CompanyProfileDto {
        val user = userService.findById(request.userId)
            ?: throw NoSuchElementException("User ${request.userId} is not found")

        if (isOwn) {
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
        } else {
            userCompanyRepository.findById(companyId!!).orElseThrow { NoSuchElementException("Company doesn't exist") }
            val userCompany = UserContractor(
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

            userCompany.inn = companyData.data.inn
            userCompany.ogrn = companyData.data.ogrn
            userCompany.fullName = companyData.value
            userCompany.address = companyData.data.address.value
            userCompany.managerName = companyData.data.management?.name
            userCompany.managerTitle = companyData.data.management?.post
            userContractorRepository.save(userCompany)
            return userCompany.toDto()
        }
    }

    override fun getCompanies(): List<CompanyProfileDto> {
        val user = userService.getCurrentUser() ?: throw NoSuchElementException("User not found")
        return userCompanyRepository.findAllByUser(user).map {
            it.toDto()
        }
    }

    override fun getCompanyById(companyId: Long): CompanyProfileDto {
        val user = userService.getCurrentUser() ?: throw NoSuchElementException("User not found")
        return userCompanyRepository.findByUserAndId(user, companyId)
            .orElseThrow { NoSuchElementException("Company not found") }
            .toDto()
    }

    override fun getContractor(companyId: Long, contractorId: Long): CompanyProfileDto {
        val user = userService.getCurrentUser() ?: throw NoSuchElementException("User not found")
        return userContractorRepository.findByIdAndCompanyIdAndUser(contractorId, companyId, user)
            .orElseThrow { NoSuchElementException("Contractor not found") }.toDto()
    }

    override fun deleteCompany(companyId: Long) {
        val company = userCompanyRepository.findById(companyId).orElseThrow { NoSuchElementException("User company is not found")}
        val contractors = userContractorRepository.findAllByCompanyId(companyId)

        documentService.deleteCompanyDocuments(company.id!!)
        contractors.forEach { documentService.deleteContractorDocuments(it.id!!) }

        userContractorRepository.deleteAll(contractors)
        userCompanyRepository.delete(company)
    }

    @Transactional
    override fun uploadDocuments(
        files: List<RawDocumentAndMetatype>,
        companyId: Long,
        contractorId: Long?,
        type: BusinessType
    ) {
        val user = when (type) {
            BusinessType.USER -> userCompanyRepository.findById(companyId)
                .orElseThrow { NoSuchElementException("Own company is not found") }.user

            BusinessType.PARTNER -> userContractorRepository.findById(contractorId!!)
                .orElseThrow { NoSuchElementException("Counterparty is not found") }.user
        }

        documentService.batchSave(
            user!!.id!!,
            type,
            companyId = companyId,
            contractorId = contractorId,
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

}