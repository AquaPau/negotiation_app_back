package org.superapp.negotiatorbot.webclient.service.documentMetadata

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.*
import org.superapp.negotiatorbot.webclient.repository.DocumentMetadataRepository
import org.superapp.negotiatorbot.webclient.service.s3.S3Service

@Transactional
interface DocumentService {

    fun save(document: DocumentMetadata): DocumentMetadata

    fun batchSave(
        userId: Long,
        businessType: BusinessType,
        relatedId: Long,
        fileData: List<RawDocumentAndMetatype>
    ): List<DocumentMetadata>

    @Throws(NoSuchElementException::class)
    fun get(serverSideFileId: Long): DocumentMetadata

    fun getDocumentList(userId: Long, companyId: Long): List<DocumentMetadataDto>

    fun getMetadataByContractorId(companyId: Long, contractorId: Long): List<DocumentMetadataDto>

    fun deleteDocument(documentId: Long)

    fun deleteContractorDocuments(contractorId: Long)

    fun deleteCompanyDocuments(companyId: Long)
}


@Service
class DocumentServiceImpl(
    private val documentMetadataRepository: DocumentMetadataRepository,
    private val s3Service: S3Service,
) : DocumentService {

    override fun save(document: DocumentMetadata): DocumentMetadata = documentMetadataRepository.save(document)


    override fun batchSave(
        userId: Long,
        businessType: BusinessType,
        relatedId: Long,
        fileData: List<RawDocumentAndMetatype>
    ): List<DocumentMetadata> {
        val files = DocumentFactory.createFiles(
            userId,
            businessType,
            relatedId,
            fileData.map {
                it.fileNameWithExtensions
            },
            fileData.map { it.documentType })
            .filter {
                !documentMetadataRepository.existsByUserIdAndName(
                    userId,
                    it.name!!
                )
            }

        files.forEachIndexed { index, file ->
            s3Service.upload(file.path!!, fileData[index].fileContent!!)
        }

        documentMetadataRepository.saveAll(files)
        return files
    }

    override fun get(serverSideFileId: Long): DocumentMetadata {
        return documentMetadataRepository.findById(serverSideFileId).orElseThrow()
    }

    override fun getDocumentList(userId: Long, companyId: Long): List<DocumentMetadataDto> {
        return documentMetadataRepository.findAllByBusinessTypeAndRelatedIdAndUserIdOrderByIdAsc(
            businessType = BusinessType.USER,
            companyId = companyId,
            userId = userId
        ).map { it.entityToDto() }

    }

    override fun getMetadataByContractorId(companyId: Long, contractorId: Long): List<DocumentMetadataDto> {
        return documentMetadataRepository.findAllByBusinessTypeAndRelatedIdAndUserIdOrderByIdAsc(
            BusinessType.PARTNER,
            contractorId,
            companyId
        )
            .map { it.entityToDto() }
    }

    override fun deleteDocument(documentId: Long) {
        val documentMetadata = documentMetadataRepository.findById(documentId).orElseThrow()
        s3Service.delete(documentMetadata.path!!)
        documentMetadataRepository.delete(documentMetadata)
    }

    override fun deleteContractorDocuments(contractorId: Long) {
        val docs = documentMetadataRepository.findAllByBusinessTypeAndRelatedId(BusinessType.PARTNER, contractorId)
        docs.forEach { s3Service.delete(it.path!!) }
        documentMetadataRepository.deleteAll(docs)
    }

    override fun deleteCompanyDocuments(companyId: Long) {
        val docs = documentMetadataRepository.findAllByBusinessTypeAndRelatedId(BusinessType.USER, companyId)
        docs.forEach { s3Service.delete(it.path!!) }
        documentMetadataRepository.deleteAll(docs)
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