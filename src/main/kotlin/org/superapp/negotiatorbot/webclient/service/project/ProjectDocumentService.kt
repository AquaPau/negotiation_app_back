package org.superapp.negotiatorbot.webclient.service.project

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.enum.BusinessType
import org.superapp.negotiatorbot.webclient.repository.project.ProjectRepository
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService

interface ProjectDocumentService {
    fun uploadDocuments(files: List<RawDocumentAndMetatype>, relatedId: Long, type: BusinessType)
    fun getDocuments(projectId: Long, project: BusinessType): List<DocumentMetadataDto>
    fun deleteDocuments(projectId: Long)
    fun deleteDocumentById(documentId: Long, projectId: Long)
}

@Service
class ProjectDocumentServiceImpl(
    private val documentService: DocumentService,
    private val projectRepository: ProjectRepository
) : ProjectDocumentService {
    override fun uploadDocuments(files: List<RawDocumentAndMetatype>, relatedId: Long, type: BusinessType) {
        val user = when (type) {
            BusinessType.PROJECT -> projectRepository.findById(relatedId).orElseThrow { NoSuchElementException() }.user
            else -> throw UnsupportedOperationException()
        }

        documentService.batchSave(
            user!!.id!!,
            type,
            relatedId,
            files
        )
    }

    override fun getDocuments(projectId: Long, project: BusinessType): List<DocumentMetadataDto> {
        val user = projectRepository.findById(projectId)
            .orElseThrow { NoSuchElementException() }.user
        return documentService.getDocumentList(user!!.id!!, projectId)
    }

    override fun deleteDocuments(projectId: Long) {
        documentService.deleteDocument(businessType = BusinessType.PROJECT, projectId)
    }

    override fun deleteDocumentById(documentId: Long, projectId: Long) {
        documentService.getDocumentByRelatedIdAndDocumentId(documentId, projectId, BusinessType.PROJECT)
        documentService.deleteDocument(documentId)
    }

}