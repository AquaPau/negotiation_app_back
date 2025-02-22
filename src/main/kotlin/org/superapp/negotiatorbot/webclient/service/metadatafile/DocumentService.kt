package org.superapp.negotiatorbot.webclient.service.metadatafile

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.*
import org.superapp.negotiatorbot.webclient.repository.DocumentMetadataRepository
import org.superapp.negotiatorbot.webclient.service.s3.S3Service

@Transactional
interface DocumentService {
    fun save(
        user: User,
        businessType: BusinessType,
        fileNameWithExtension: String,
        multipartFile: MultipartFile
    ): DocumentMetadata

    fun save(document: DocumentMetadata): DocumentMetadata

    fun batchSave(
        userId: Long,
        businessType: BusinessType,
        companyId: Long,
        contractorId: Long? = null,
        fileData: List<RawDocumentAndMetatype>
    ): List<DocumentMetadata>

    @Throws(NoSuchElementException::class)
    fun get(serverSideFileId: Long): DocumentMetadata

    fun getDocumentList(userId: Long, companyId: Long): List<DocumentMetadataDto>

    fun getMetadataByCounterPartyId(companyId: Long, counterPartyId: Long): List<DocumentMetadataDto>

    fun deleteDocument(documentId: Long)

    fun deleteContractorDocuments(contractorId: Long)

    fun deleteCompanyDocuments(companyId: Long)
}


@Service
class DocumentServiceImpl(
    private val documentMetadataRepository: DocumentMetadataRepository,
    private val s3Service: S3Service,
) : DocumentService {

    override fun save(document: DocumentMetadata) = documentMetadataRepository.save(document)


    @Transactional
    override fun save(
        user: User,
        businessType: BusinessType,
        fileNameWithExtension: String,
        multipartFile: MultipartFile
    ): DocumentMetadata {
        val file = DocumentHelper.createFile(user, businessType, fileNameWithExtension)
        documentMetadataRepository.save(file)
        s3Service.upload(file.path!!, multipartFile.bytes)
        return file
    }

    override fun batchSave(
        userId: Long,
        businessType: BusinessType,
        companyId: Long,
        contractorId: Long?,
        fileData: List<RawDocumentAndMetatype>
    ): List<DocumentMetadata> {
        val files = DocumentHelper.createFiles(
            userId,
            businessType,
            companyId = companyId,
            contractorId = contractorId,
            fileData.map {
                it.fileNameWithExtensions
            },
            fileData.map { it.documentType })
            .filter {
                !documentMetadataRepository.existsByUserIdAndName(
                    userId,
                    it.name!!
                )
            } //to prevent second download and saving to DB

        documentMetadataRepository.saveAll(files)

        files.forEachIndexed { index, file ->
            s3Service.upload(file.path!!, fileData[index].fileContent!!)
        }
        return files
    }

    override fun get(serverSideFileId: Long): DocumentMetadata {
        return documentMetadataRepository.findById(serverSideFileId).orElseThrow()
    }

    override fun getDocumentList(userId: Long, companyId: Long): List<DocumentMetadataDto> {
        return documentMetadataRepository.findAllByBusinessTypeAndCompanyIdAndUserIdOrderByIdAsc(
            businessType = BusinessType.USER,
            companyId = companyId,
            userId = userId
        ).map(entityToDto())

    }

    override fun getMetadataByCounterPartyId(companyId: Long, counterPartyId: Long): List<DocumentMetadataDto> {
        return documentMetadataRepository.findAllByBusinessTypeAndCounterPartyIdAndCompanyIdOrderByIdAsc(
            BusinessType.PARTNER,
            counterPartyId,
            companyId
        )
            .map(entityToDto())
    }

    override fun deleteDocument(documentId: Long) {
        val documentMetadata = documentMetadataRepository.findById(documentId).orElseThrow()
        s3Service.delete(documentMetadata.path!!)
        documentMetadataRepository.delete(documentMetadata)
    }

    override fun deleteContractorDocuments(contractorId: Long) {
        val docs = documentMetadataRepository.findAllByCounterPartyId(contractorId)
        docs.forEach { s3Service.delete(it.path!!) }
        documentMetadataRepository.deleteAll(docs)
    }

    override fun deleteCompanyDocuments(companyId: Long) {
        val docs = documentMetadataRepository.findAllByCompanyIdAndCounterPartyIdIsNull(companyId)
        docs.forEach { s3Service.delete(it.path!!) }
        documentMetadataRepository.deleteAll(docs)
    }

    private fun entityToDto(): (DocumentMetadata) -> DocumentMetadataDto =
        {
            DocumentMetadataDto(
                id = it.id!!,
                name = "${it.name!!}.${it.extension}",
                userId = it.userId!!,
                counterPartyId = it.counterPartyId,
                description = it.description,
                risks = it.risks,
                companyId = it.companyId,
                type = it.documentType!!
            )
        }
}