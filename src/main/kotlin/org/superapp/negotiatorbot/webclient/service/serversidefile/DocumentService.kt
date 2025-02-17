package org.superapp.negotiatorbot.webclient.service.serversidefile

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.repository.DocumentMetadataRepository
import org.superapp.negotiatorbot.webclient.service.s3.S3Service
import java.io.File

@Transactional
interface DocumentService {
    fun save(
        user: User,
        businessType: BusinessType,
        fileNameWithExtension: String,
        multipartFile: MultipartFile
    ): DocumentMetadata

    fun batchSave(
        user: User,
        businessType: BusinessType,
        companyId: Long,
        fileData: List<RawDocumentAndMetatype>
    ): List<DocumentMetadata>

    @Throws(NoSuchElementException::class)
    fun get(serverSideFileId: Long): File

    fun getMetadataByCounterPartyId(counterPartyId: Long): List<DocumentMetadataDto>
}


@Service
class DocumentServiceImpl(
    private val documentMetadataRepository: DocumentMetadataRepository,
    private val s3Service: S3Service,
    private val entityManager: EntityManager,
) : DocumentService {

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
        user: User,
        businessType: BusinessType,
        companyId: Long,
        fileData: List<RawDocumentAndMetatype>
    ): List<DocumentMetadata> {
        val files = DocumentHelper.createFiles(userId, businessType, companyId, fileData.map {
            it.fileNameWithExtensions
        }, fileData.map { it.documentType })
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

    override fun get(serverSideFileId: Long): File {
        val file = documentMetadataRepository.findById(serverSideFileId).orElseThrow()
        return s3Service.download(file.path!!).file
    }

    override fun getMetadataByCounterPartyId(counterPartyId: Long): List<DocumentMetadataDto> {
        return documentMetadataRepository.findAllByBusinessTypeAndCounterPartyId(BusinessType.PARTNER, counterPartyId)
            .map {
                DocumentMetadataDto(
                    id = it.id!!,
                    name = it.name!!,
                    userId = it.user!!.id!!,
                    counterPartyId = it.counterPartyId,
                    description = it.description
                )
            }
    }
}