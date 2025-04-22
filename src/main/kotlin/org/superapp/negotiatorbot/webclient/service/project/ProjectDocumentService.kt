package org.superapp.negotiatorbot.webclient.service.project

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.document.DocumentProjectMetadataDto
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import org.superapp.negotiatorbot.webclient.exception.ProjectNotFoundException
import org.superapp.negotiatorbot.webclient.repository.project.ProjectRepository
import org.superapp.negotiatorbot.webclient.service.EnterpriseDocumentService
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService

interface ProjectDocumentService : EnterpriseDocumentService<Project> {
    fun uploadDocuments(files: List<RawDocumentAndMetatype>, relatedId: Long)
    fun getDocuments(projectId: Long, businessType: BusinessType): List<DocumentProjectMetadataDto>
    fun deleteDocuments(projectId: Long)
    fun deleteDocumentById(documentId: Long, projectId: Long)
}

@Service
class ProjectDocumentServiceImpl(
    private val documentService: DocumentService,
    private val projectRepository: ProjectRepository
) : ProjectDocumentService {
    override fun uploadDocuments(files: List<RawDocumentAndMetatype>, relatedId: Long) {
        val user = projectRepository.findById(relatedId).orElseThrow { ProjectNotFoundException(null, relatedId) }.user

        documentService.batchSave(
            user!!.id!!,
            BusinessType.PROJECT,
            relatedId,
            files
        )
    }

    override fun getDocuments(projectId: Long, businessType: BusinessType): List<DocumentProjectMetadataDto> {
        val user = projectRepository.findById(projectId)
            .orElseThrow { ProjectNotFoundException(projectId) }.user
        return documentService.getProjectDocumentListDto(
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