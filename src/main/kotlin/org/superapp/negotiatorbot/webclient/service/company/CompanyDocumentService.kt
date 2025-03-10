package org.superapp.negotiatorbot.webclient.service.company

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.enum.BusinessType
import org.superapp.negotiatorbot.webclient.repository.company.UserCompanyRepository
import org.superapp.negotiatorbot.webclient.repository.company.UserContractorRepository
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService

interface CompanyDocumentService {
    fun uploadDocuments(
        files: List<RawDocumentAndMetatype>,
        relatedId: Long,
        type: BusinessType
    )

    fun getDocuments(companyId: Long, type: BusinessType): List<DocumentMetadataDto>

    fun deleteContractorDocuments(contractorId: Long)

    fun deleteCompanyDocuments(companyId: Long)

    fun deleteDocumentById(documentId: Long, companyId: Long)

    fun deleteDocumentById(documentId: Long, companyId: Long, contractorId: Long)

    fun getMetadataByContractorId(companyId: Long, contractorId: Long): List<DocumentMetadataDto>

}

@Service
class CompanyDocumentServiceImpl(
    private val documentService: DocumentService,
    private val userCompanyRepository: UserCompanyRepository,
    private val userContractorRepository: UserContractorRepository
) : CompanyDocumentService {

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

            else -> throw UnsupportedOperationException()
        }

        documentService.batchSave(
            user!!.id!!,
            type,
            relatedId,
            files
        )

    }

    override fun getDocuments(companyId: Long, type: BusinessType): List<DocumentMetadataDto> {
        val user = userCompanyRepository.findById(companyId)
            .orElseThrow { NoSuchElementException("Company is not found") }.user
        return documentService.getDocumentList(user!!.id!!, companyId)
    }

    override fun deleteContractorDocuments(contractorId: Long) {
        documentService.deleteDocument(businessType = BusinessType.PARTNER, contractorId)
    }

    override fun deleteCompanyDocuments(companyId: Long) {
        documentService.deleteDocument(businessType = BusinessType.USER, companyId)
    }

    override fun deleteDocumentById(documentId: Long, companyId: Long) {
        documentService.getDocumentByRelatedIdAndDocumentId(documentId, companyId, BusinessType.USER)
        documentService.deleteDocument(documentId)
    }

    override fun deleteDocumentById(documentId: Long, companyId: Long, contractorId: Long) {
        documentService.getDocumentByRelatedIdAndDocumentId(documentId, contractorId, BusinessType.PARTNER)
        documentService.deleteDocument(documentId)
    }

    override fun getMetadataByContractorId(companyId: Long, contractorId: Long): List<DocumentMetadataDto> {
        userCompanyRepository.findById(companyId).orElseThrow { NoSuchElementException() }
        userContractorRepository.findById(contractorId).orElseThrow { NoSuchElementException() }
        return documentService.getMetadataByContractorId(companyId, contractorId)
    }
}