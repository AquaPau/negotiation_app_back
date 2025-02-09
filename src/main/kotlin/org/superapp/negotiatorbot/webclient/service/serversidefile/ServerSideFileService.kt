package org.superapp.negotiatorbot.webclient.service.serversidefile

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.entity.ServerSideFile
import org.superapp.negotiatorbot.webclient.entity.User
import java.util.StringJoiner

interface ServerSideFileService {}



@Service
class ServerSideFileServiceImpl(private val serverSideFileFactory: ServerSideFileFactory) : ServerSideFileService {

    @Transactional
    fun save(
        user: User,
        businessType: BusinessType,
        fileNameWithExtension: String,
        multipartFile: MultipartFile
    ): ServerSideFile {
        TODO("Not impl")
    }



}