package org.superapp.negotiatorbot.webclient.service.company

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.CounterpartyDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.dto.dadata.DadataRequest
import org.superapp.negotiatorbot.webclient.dto.document.DocumentDataWithName
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.entity.UserCompany
import org.superapp.negotiatorbot.webclient.entity.UserCounterparty
import org.superapp.negotiatorbot.webclient.entity.toThinDto
import org.superapp.negotiatorbot.webclient.enum.CompanyRegion
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import org.superapp.negotiatorbot.webclient.port.DadataPort
import org.superapp.negotiatorbot.webclient.repository.UserCompanyRepository
import org.superapp.negotiatorbot.webclient.repository.UserCounterpartyRepository
import org.superapp.negotiatorbot.webclient.repository.UserRepository
import org.superapp.negotiatorbot.webclient.service.functiona.OpenAiUserService
import org.superapp.negotiatorbot.webclient.service.serversidefile.DocumentService

interface CompanyService {

    fun createCompany(request: NewCompanyProfile): CompanyProfileDto

    suspend fun uploadDocuments(
        files: List<MultipartFile>,
        documentTypes: List<DocumentType>,
        companyId: Long,
        type: BusinessType
    )

    fun getCounterparties(companyId: Long): List<CounterpartyDto>
}

@Service
class CompanyServiceImpl(
    private val userRepository: UserRepository,
    private val userCompanyRepository: UserCompanyRepository,
    private val userCounterpartyRepository: UserCounterpartyRepository,
    private val documentService: DocumentService,
    private val openAiUserService: OpenAiUserService,
    private val dadataPort: DadataPort,
    @Value("\${dadata.token}") private val dadataToken: String
) : CompanyService {
    override fun createCompany(request: NewCompanyProfile): CompanyProfileDto {
        val user = userRepository.findById(request.userId).orElseThrow {
            NoSuchElementException("User is not found")
        }
        if (request.isOwn) {
            val userCompany = UserCompany(
                user = user,
                customUserGeneratedName = request.customUserGeneratedName,
                residence = CompanyRegion.RU
            )
            userCompanyRepository.save(userCompany)
            return userCompany.toThinDto()
        } else {
            val userCompany = UserCounterparty(
                user = user,
                customUserGeneratedName = request.customUserGeneratedName,
                residence = CompanyRegion.RU
            )
            userCounterpartyRepository.save(userCompany)
            return userCompany.toThinDto()
        }
    }

    override suspend fun uploadDocuments(
        files: List<MultipartFile>,
        documentTypes: List<DocumentType>,
        companyId: Long,
        type: BusinessType
    ) {
        val user = when (type) {
            BusinessType.USER -> userCompanyRepository.findById(companyId)
                .orElseThrow { NoSuchElementException("Own company is not found") }.user

            BusinessType.PARTNER -> userCounterpartyRepository.findById(companyId)
                .orElseThrow { NoSuchElementException("Counterparty is not found") }.user
        }
        documentService.batchSave(
            user,
            type,
            companyId,
            documentTypes.mapIndexed { index, documentType -> RawDocumentAndMetatype(documentType, files[index]) })

        // here we should load documents with the prompt of extract company data to chatgpt and then update info about
        // company in database
        val result = openAiUserService.uploadFilesAndExtractCompanyData(
            user.id!!,
            files.map { DocumentDataWithName(it.inputStream, it.originalFilename ?: "$companyId file") },
            prompt = "here will be a useful prompt to extract only company data for dadata"
        )
        openAiUserService.deleteFilesFromOpenAi(user.id!!)

        val companyData = dadataPort.findCompanyByInn(
            DadataRequest(query = result), token = dadataToken
        ).suggestions.firstOrNull()
            ?: throw NoSuchElementException("Data about company $companyId is not found in dadata")

        when (type) {
            BusinessType.USER -> {
                val company = userCompanyRepository.findById(companyId)
                    .orElseThrow { NoSuchElementException("Company is not found") }
                company.inn = companyData.data.inn
                company.ogrn = companyData.data.ogrn
                company.fullName = companyData.value
                company.address = companyData.data.address.value
                company.managerName = companyData.data.management.name
                company.managerTitle = companyData.data.management.post
                userCompanyRepository.save(company)
            }

            BusinessType.PARTNER -> {
                val company = userCounterpartyRepository.findById(companyId)
                    .orElseThrow { NoSuchElementException("Counterparty is not found") }
                company.inn = companyData.data.inn
                company.ogrn = companyData.data.ogrn
                company.fullName = companyData.value
                company.address = companyData.data.address.value
                company.managerName = companyData.data.management.name
                company.managerTitle = companyData.data.management.post
                userCounterpartyRepository.save(company)
            }
        }

    }

    override fun getCounterparties(companyId: Long): List<CounterpartyDto> {
        val user = userCompanyRepository.findById(companyId)
            .orElseThrow { NoSuchElementException("Company is not found") }.user
        return userCounterpartyRepository.findAllByUser(user).map {
            CounterpartyDto(it.id!!, it.customUserGeneratedName)
        }
    }

}