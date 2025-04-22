package org.superapp.negotiatorbot.webclient.service.contractor

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.document.DocumentEnterpriseMetadataDto
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.UserContractor
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import org.superapp.negotiatorbot.webclient.exception.CompanyNotFoundException
import org.superapp.negotiatorbot.webclient.exception.ContractorNotFoundException
import org.superapp.negotiatorbot.webclient.repository.company.UserCompanyRepository
import org.superapp.negotiatorbot.webclient.repository.company.UserContractorRepository
import org.superapp.negotiatorbot.webclient.service.EnterpriseDocumentService
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService

interface ContractorDocumentService : EnterpriseDocumentService<UserContractor> {

    fun uploadDocuments(
        files: List<RawDocumentAndMetatype>,
        relatedId: Long
    )

    fun getDocuments(companyId: Long, contractorId: Long): List<DocumentEnterpriseMetadataDto>

    fun getDocument(contractorId: Long, documentId: Long): DocumentEnterpriseMetadataDto

    fun deleteDocuments(contractorId: Long)

    fun deleteDocumentById(documentId: Long, companyId: Long, contractorId: Long)
}

@Service
class ContractorDocumentServiceImpl(
    private val documentService: DocumentService,
    private val userCompanyRepository: UserCompanyRepository,
    private val userContractorRepository: UserContractorRepository
) : ContractorDocumentService {
    override fun uploadDocuments(files: List<RawDocumentAndMetatype>, relatedId: Long) {

        val user = userContractorRepository.findById(relatedId)
            .orElseThrow { ContractorNotFoundException(relatedId) }.user

        documentService.batchSave(
            user.id!!,
            BusinessType.PARTNER,
            relatedId,
            files
        )

    }

    override fun getDocuments(companyId: Long, contractorId: Long): List<DocumentEnterpriseMetadataDto> {
        val company = userCompanyRepository.findById(companyId).orElseThrow { CompanyNotFoundException(companyId) }
        userContractorRepository.findById(contractorId).orElseThrow { ContractorNotFoundException(contractorId) }
        return documentService.getEnterpriseDocumentListDto(
            userId = company.user!!.id!!,
            relatedId = contractorId,
            businessType = BusinessType.PARTNER
        )
    }

    override fun getDocument(contractorId: Long, documentId: Long): DocumentEnterpriseMetadataDto {
        userContractorRepository.findById(contractorId)
            .orElseThrow { ContractorNotFoundException(contractorId) }.user
        return documentService.getEnterpriseDocumentById(
            relatedId = contractorId,
            documentId = documentId,
            businessType = BusinessType.PARTNER
        )
    }

    override fun deleteDocuments(contractorId: Long) {
        documentService.deleteDocument(businessType = BusinessType.PARTNER, contractorId)
    }

    override fun deleteDocumentById(documentId: Long, companyId: Long, contractorId: Long) {
        documentService.getDocumentByRelatedIdAndDocumentId(documentId, contractorId, BusinessType.PARTNER)
        documentService.deleteDocument(documentId)
    }

}