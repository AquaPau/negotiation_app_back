package org.superapp.negotiatorbot.webclient.service.documentMetadata

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.*
import org.superapp.negotiatorbot.webclient.enum.BusinessType
import org.superapp.negotiatorbot.webclient.repository.DocumentMetadataRepository
import org.superapp.negotiatorbot.webclient.service.s3.S3Service

@Transactional
interface DocumentService {

    fun save(document: DocumentMetadata): DocumentMetadata

    fun batchSave(
        userId: Long, businessType: BusinessType, relatedId: Long, fileData: List<RawDocumentAndMetatype>
    ): List<DocumentMetadata>

    @Throws(NoSuchElementException::class)
    fun get(serverSideFileId: Long): DocumentMetadata

    fun getDocumentList(relatedId: Long, userId: Long, businessType: BusinessType): List<DocumentMetadataDto>

    fun deleteDocument(documentId: Long)

    fun deleteDocument(businessType: BusinessType, id: Long)

    fun getDocumentByRelatedIdAndDocumentId(
        documentId: Long,
        relatedId: Long,
        businessType: BusinessType
    ): DocumentMetadata
}


@Service
class DocumentServiceImpl(
    private val documentMetadataRepository: DocumentMetadataRepository,
    private val s3Service: S3Service,
) : DocumentService {

    override fun save(document: DocumentMetadata): DocumentMetadata = documentMetadataRepository.save(document)


    override fun batchSave(
        userId: Long, businessType: BusinessType, relatedId: Long, fileData: List<RawDocumentAndMetatype>
    ): List<DocumentMetadata> {
        val files = DocumentFactory.createFiles(userId, businessType, relatedId, fileData.map {
            it.fileNameWithExtensions
        }, fileData.map { it.documentType }).filter {
            !documentMetadataRepository.existsByUserIdAndName(
                userId, it.name!!
            )
        }

        files.forEachIndexed { index, file ->
            s3Service.uploadToS3(file, fileData[index])
        }

        documentMetadataRepository.saveAll(files)
        return files
    }

    override fun get(serverSideFileId: Long): DocumentMetadata {
        return documentMetadataRepository.findById(serverSideFileId).orElseThrow()
    }

    override fun getDocumentList(relatedId: Long, userId: Long, businessType: BusinessType): List<DocumentMetadataDto> {
        return documentMetadataRepository.findAllByBusinessTypeAndRelatedIdAndUserIdOrderByIdAsc(
            businessType = businessType,
            relatedId = relatedId,
            userId = userId
        ).map { it.entityToDto() }
    }

    override fun deleteDocument(documentId: Long) {
        val documentMetadata = documentMetadataRepository.findById(documentId).orElseThrow()
        s3Service.delete(documentMetadata)
        documentMetadataRepository.delete(documentMetadata)
    }

    override fun deleteDocument(businessType: BusinessType, id: Long) {
        val docs = documentMetadataRepository.findAllByBusinessTypeAndRelatedId(businessType, id)
        docs.forEach { s3Service.delete(it) }
        documentMetadataRepository.deleteAll(docs)
    }

    override fun getDocumentByRelatedIdAndDocumentId(
        documentId: Long,
        relatedId: Long,
        businessType: BusinessType
    ): DocumentMetadata {
        return documentMetadataRepository.findByIdAndBusinessTypeAndRelatedId(documentId, businessType, relatedId)
            .orElseThrow {
                NoSuchElementException()
            }
    }

    private fun DocumentMetadata.entityToDto(): DocumentMetadataDto {
        val result = DocumentMetadataDto(
            id = this.id!!,
            name = "${this.name!!}.${this.extension}",
            userId = this.userId!!,
            description = this.description,
            risks = this.risks,
            type = this.documentType!!
        )
        when (this.businessType!!) {
            BusinessType.USER -> result.companyId = this.relatedId
            BusinessType.PARTNER -> result.counterPartyId = this.relatedId
            else -> TODO()
        }
        return result
    }
}