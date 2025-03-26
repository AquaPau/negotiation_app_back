package org.superapp.negotiatorbot.webclient.service.documentMetadata

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.superapp.negotiatorbot.webclient.dto.document.DescriptionData
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.dto.document.RisksData
import org.superapp.negotiatorbot.webclient.entity.*
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.superapp.negotiatorbot.webclient.enums.TaskType.*
import org.superapp.negotiatorbot.webclient.repository.DocumentMetadataRepository
import org.superapp.negotiatorbot.webclient.repository.TaskRecordRepository
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

    fun getDocumentById(relatedId: Long, documentId: Long, businessType: BusinessType): DocumentMetadataDto

    fun deleteDocument(documentId: Long)

    fun deleteDocument(businessType: BusinessType, id: Long)

    fun getDocumentByRelatedIdAndDocumentId(
        documentId: Long,
        relatedId: Long,
        businessType: BusinessType
    ): DocumentMetadata
}


@Service
@Transactional
class DocumentServiceImpl(
    private val documentMetadataRepository: DocumentMetadataRepository,
    private val s3Service: S3Service,
    private val taskRecordRepository: TaskRecordRepository
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

        val documents = documentMetadataRepository.findAllByBusinessTypeAndRelatedIdAndUserIdOrderByIdAsc(
            businessType = businessType,
            relatedId = relatedId,
            userId = userId
        )
        val recordsByDocument = taskRecordRepository.findAllByRelatedIdInAndTaskTypeIn(
            documents.map { it.id!! }, ELIGIBLE_TASK_TYPES
        )
            .groupBy { it.relatedId }.entries.associate { it.key to it.value.sortedByDescending { task -> task.id } }

        return documents.map { it.entityToDto(recordsByDocument[it.id!!] ?: listOf()) }
    }

    override fun getDocumentById(relatedId: Long, documentId: Long, businessType: BusinessType): DocumentMetadataDto {
        val document = documentMetadataRepository.findByIdAndBusinessTypeAndRelatedId(
            documentId,
            businessType, relatedId
        ).orElseThrow { NoSuchElementException() }
        val recordsByDocument = taskRecordRepository.findAllByRelatedIdInAndTaskTypeIn(
            listOf(document.id!!), ELIGIBLE_TASK_TYPES
        ).sortedByDescending { it.id }
        return document.entityToDto(recordsByDocument)
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


    companion object {
        private val ELIGIBLE_TASK_TYPES = listOf(
            CONTRACTOR_DOCUMENT_DESCRIPTION,
            CONTRACTOR_DOCUMENT_RISKS,
            COMPANY_DOCUMENT_DESCRIPTION,
            COMPANY_DOCUMENT_DESCRIPTION
        )
    }

    /*
    The aiResults list comes descending by id, so the freshest one will be used as a result for the user others will
    be historical and will be reduced
     */
    private fun DocumentMetadata.entityToDto(aiResults: List<TaskRecord>): DocumentMetadataDto {
        val result = DocumentMetadataDto(
            id = this.id!!,
            name = "${this.name!!}.${this.extension}",
            userId = this.userId!!,
            type = this.documentType!!,
            description = aiResults.firstOrNull {
                it.relatedId == this.id!! && it.taskType.toPromptType() == PromptType.DESCRIPTION
            }?.let { DescriptionData(it.result, it.status, it.id!!) },
            risks = aiResults.firstOrNull {
                it.relatedId == this.id!! && it.taskType.toPromptType() == PromptType.RISKS
            }?.let { RisksData(it.result, it.status, it.id!!) }
        )
        when (this.businessType!!) {
            BusinessType.USER -> result.companyId = this.relatedId
            BusinessType.PARTNER -> result.counterPartyId = this.relatedId
            else -> throw UnsupportedOperationException()
        }
        return result
    }
}