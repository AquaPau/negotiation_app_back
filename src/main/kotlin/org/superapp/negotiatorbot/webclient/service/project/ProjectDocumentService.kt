package org.superapp.negotiatorbot.webclient.service.project

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import org.superapp.negotiatorbot.webclient.repository.project.ProjectRepository
import org.superapp.negotiatorbot.webclient.service.EnterpriseDocumentService
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService

interface ProjectDocumentService : EnterpriseDocumentService<Project> {
    fun uploadDocuments(files: List<RawDocumentAndMetatype>, relatedId: Long, type: BusinessType)
    fun getDocuments(projectId: Long, businessType: BusinessType): List<DocumentMetadataDto>
    fun deleteDocuments(projectId: Long)
    fun deleteDocumentById(documentId: Long, projectId: Long)
}

@Service
class ProjectDocumentServiceImpl(
    private val documentService: DocumentService,
    private val projectRepository: ProjectRepository
) : ProjectDocumentService {
    override fun uploadDocuments(files: List<RawDocumentAndMetatype>, relatedId: Long, type: BusinessType) {
        val user = projectRepository.findById(relatedId).orElseThrow { NoSuchElementException() }.user

        documentService.batchSave(
            user!!.id!!,
            type,
            relatedId,
            files
        )
    }

    override fun getDocuments(projectId: Long, businessType: BusinessType): List<DocumentMetadataDto> {
        val user = projectRepository.findById(projectId)
            .orElseThrow { NoSuchElementException() }.user
        return documentService.getDocumentList(
            userId = user!!.id!!,
            relatedId = projectId,
            businessType = BusinessType.PROJECT
        )
    }

    override fun deleteDocuments(projectId: Long) {
        documentService.deleteDocument(businessType = BusinessType.PROJECT, projectId)
    }

    override fun deleteDocumentById(documentId: Long, projectId: Long) {
        documentService.getDocumentByRelatedIdAndDocumentId(documentId, projectId, BusinessType.PROJECT)
        documentService.deleteDocument(documentId)
    }

}