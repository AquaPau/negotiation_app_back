package org.superapp.negotiatorbot.webclient.service.company

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.document.DocumentEnterpriseMetadataDto
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.UserCompany
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import org.superapp.negotiatorbot.webclient.exception.CompanyNotFoundException
import org.superapp.negotiatorbot.webclient.repository.company.UserCompanyRepository
import org.superapp.negotiatorbot.webclient.service.EnterpriseDocumentService
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService

interface CompanyDocumentService : EnterpriseDocumentService<UserCompany> {
    fun uploadDocuments(
        files: List<RawDocumentAndMetatype>,
        relatedId: Long
    )

    fun getDocuments(companyId: Long, type: BusinessType): List<DocumentEnterpriseMetadataDto>

    fun getDocument(companyId: Long, documentId: Long): DocumentEnterpriseMetadataDto


    fun deleteCompanyDocuments(companyId: Long)

    fun deleteDocumentById(documentId: Long, companyId: Long)

}

@Service
class CompanyDocumentServiceImpl(
    private val documentService: DocumentService,
    private val userCompanyRepository: UserCompanyRepository
) : CompanyDocumentService {

    override fun uploadDocuments(
        files: List<RawDocumentAndMetatype>,
        relatedId: Long
    ) {
        val user = userCompanyRepository.findById(relatedId)
            .orElseThrow { CompanyNotFoundException(relatedId) }.user

        documentService.batchSave(
            user!!.id!!,
            BusinessType.USER,
            relatedId,
            files
        )

    }

    override fun getDocuments(companyId: Long, type: BusinessType): List<DocumentEnterpriseMetadataDto> {
        val user = userCompanyRepository.findById(companyId)
            .orElseThrow { CompanyNotFoundException(companyId) }.user
        return documentService.getEnterpriseDocumentListDto(
            userId = user!!.id!!,
            relatedId = companyId,
            businessType = BusinessType.USER
        )
    }

    override fun getDocument(companyId: Long, documentId: Long): DocumentEnterpriseMetadataDto {
        userCompanyRepository.findById(companyId)
            .orElseThrow { CompanyNotFoundException(companyId) }.user
        return documentService.getEnterpriseDocumentById(
            relatedId = companyId,
            documentId = documentId,
            businessType = BusinessType.USER
        )
    }

    override fun deleteCompanyDocuments(companyId: Long) {
        documentService.deleteDocument(businessType = BusinessType.USER, companyId)
    }

    override fun deleteDocumentById(documentId: Long, companyId: Long) {
        documentService.getDocumentByRelatedIdAndDocumentId(documentId, companyId, BusinessType.USER)
        documentService.deleteDocument(documentId)
    }


}