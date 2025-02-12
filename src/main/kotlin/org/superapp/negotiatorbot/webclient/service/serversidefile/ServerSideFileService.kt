package org.superapp.negotiatorbot.webclient.service.serversidefile

import io.awspring.cloud.s3.S3Resource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.entity.ServerSideFile
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.repository.ServerSideFileRepository
import org.superapp.negotiatorbot.webclient.service.s3.S3Service

interface ServerSideFileService {
    fun save(
        user: User,
        businessType: BusinessType,
        fileNameWithExtension: String,
        multipartFile: MultipartFile
    ): ServerSideFile

    @Throws(NoSuchElementException::class)
    fun get(serverSideFileId: Long) : S3Resource
}


@Service
class ServerSideFileServiceImpl(
    private val serverSideFileFactory: ServerSideFileFactory,
    private val serverSideFileRepository: ServerSideFileRepository,
    private val s3Service: S3Service
) : ServerSideFileService {

    @Transactional
    override fun save(
        user: User,
        businessType: BusinessType,
        fileNameWithExtension: String,
        multipartFile: MultipartFile
    ): ServerSideFile {
        val file = serverSideFileFactory.createFile(user, businessType, fileNameWithExtension)
        serverSideFileRepository.save(file)
        s3Service.upload(file.path!!, multipartFile)
        return file;
    }

    override fun get(serverSideFileId: Long): S3Resource {
        val file = serverSideFileRepository.findById(serverSideFileId).orElseThrow()
        return s3Service.download(file.path!!)
    }
}