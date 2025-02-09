package org.superapp.negotiatorbot.webclient.service.serversidefile

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.entity.ServerSideFile
import org.superapp.negotiatorbot.webclient.entity.User
import java.util.*
import kotlin.jvm.Throws

const val ROOT_S3 = "users"
const val S3_DELIMITER = "/"
const val EXTENSION_DELIMITER = '.'

interface ServerSideFileFactory {
    @Throws(IllegalArgumentException::class)
    fun createFile(
        user: User,
        businessType: BusinessType,
        fileNameWithExtension: String,
    ): ServerSideFile
}

@Service
class ServerSideFileFactoryImpl : ServerSideFileFactory {
    val supportedExtensions = setOf("csv", "json", "docx", "txt", "md", "pdf", "tex", "xml", "html")

    @Throws(IllegalArgumentException::class)
    override fun createFile(
        user: User,
        businessType: BusinessType,
        fileNameWithExtension: String,
    ): ServerSideFile {
        val newFile = ServerSideFile()
        newFile.user = user
        newFile.setNameAndExtension(fileNameWithExtension)
        newFile.businessType = businessType
        newFile.setPath(user, fileNameWithExtension)
        return newFile
    }

    @Throws(IllegalArgumentException::class)
    private fun ServerSideFile.setNameAndExtension(fileNameWithExtension: String) {
        val extensionIndex = extensionIndex(fileNameWithExtension)

        val extension = fileNameWithExtension.substring(extensionIndex + 1)
        val fileName = fileNameWithExtension.substring(0, extensionIndex)

        validateExtension(extension)
        validateName(fileName)

        this.extension = extension
        this.name = fileName
    }

    @Throws(IllegalArgumentException::class)
    private fun validateExtension(extension: String) {
        if (!supportedExtensions.contains(extension)) {
            throw IllegalArgumentException("Invalid extension [$extension], supported: $supportedExtensions")
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun validateName(name: String) {
        if (name.isBlank()) {
            throw IllegalArgumentException("Invalid name [$name], cannot be blank")
        }
    }


    private fun ServerSideFile.setPath(user: User, fileNameWithExtension: String) {
        val s3Path =
            StringJoiner(S3_DELIMITER).add(ROOT_S3).add(user.id.toString()).add(fileNameWithExtension).toString()
        this.path = s3Path
    }

    @Throws(IllegalArgumentException::class)
    private fun extensionIndex(fileNameWithExtension: String): Int {
        val index = fileNameWithExtension.lastIndexOf(EXTENSION_DELIMITER)
        return if (index == -1) throw IllegalArgumentException("FileName with extension must contain at least one [$EXTENSION_DELIMITER]") else index
    }

}