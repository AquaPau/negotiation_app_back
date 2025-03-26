package org.superapp.negotiatorbot.webclient.service.contractor

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.UserContractor
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import org.superapp.negotiatorbot.webclient.repository.company.UserCompanyRepository
import org.superapp.negotiatorbot.webclient.repository.company.UserContractorRepository
import org.superapp.negotiatorbot.webclient.service.EnterpriseDocumentService
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService

interface ContractorDocumentService : EnterpriseDocumentService<UserContractor> {

    fun uploadDocuments(
        files: List<RawDocumentAndMetatype>,
        relatedId: Long
    )

    fun getDocuments(companyId: Long, contractorId: Long): List<DocumentMetadataDto>

    fun getDocument(contractorId: Long, documentId: Long): DocumentMetadataDto

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
            .orElseThrow { NoSuchElementException("Counterparty is not found") }.user

        documentService.batchSave(
            user.id!!,
            BusinessType.PARTNER,
            relatedId,
            files
        )

    }

    override fun getDocuments(companyId: Long, contractorId: Long): List<DocumentMetadataDto> {
        val company = userCompanyRepository.findById(companyId).orElseThrow { NoSuchElementException() }
        userContractorRepository.findById(contractorId).orElseThrow { NoSuchElementException() }
        return documentService.getDocumentList(
            userId = company.user!!.id!!,
            relatedId = contractorId,
            businessType = BusinessType.PARTNER
        )
    }

    override fun getDocument(contractorId: Long, documentId: Long): DocumentMetadataDto {
        userContractorRepository.findById(contractorId)
            .orElseThrow { NoSuchElementException("Company is not found") }.user
        return documentService.getDocumentById(
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