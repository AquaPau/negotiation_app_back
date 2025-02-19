package org.superapp.negotiatorbot.webclient.service.serversidefile

import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import java.util.*


class DocumentHelper {


    companion object {
        private const val ROOT_S3 = "users"
        private const val S3_DELIMITER = "/"
        const val EXTENSION_DELIMITER = '.'

        private val supportedExtensions = setOf("application/pdf", "csv", "json", "docx", "txt", "md", "pdf", "tex", "xml", "html")

        @Throws(IllegalArgumentException::class)
        fun createFile(
            user: User,
            businessType: BusinessType,
            fileNameWithExtension: String,
        ): DocumentMetadata {
            val newFile = DocumentMetadata()
            newFile.userId = user.id!!
            newFile.setNameAndExtension(fileNameWithExtension)
            newFile.businessType = businessType
            newFile.setPath(user.id!!, fileNameWithExtension)
            return newFile
        }


        fun createFiles(
            userId: Long,
            businessType: BusinessType,
            companyId: Long,
            fileNameWithExtension: List<String>,
            documentTypes: List<DocumentType>
        ): List<DocumentMetadata> {
            return fileNameWithExtension.mapIndexed { index, it ->
                val newFile = DocumentMetadata()
                newFile.userId = userId
                newFile.companyId = companyId
                newFile.setNameAndExtension(it)
                newFile.businessType = businessType
                newFile.documentType = documentTypes[index]
                newFile.setPath(userId, it)
                if (businessType == BusinessType.PARTNER) newFile.counterPartyId = companyId
                newFile
            }
        }


        @Throws(IllegalArgumentException::class)
        private fun DocumentMetadata.setNameAndExtension(fileNameWithExtension: String) {
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


        private fun DocumentMetadata.setPath(userId: Long, fileNameWithExtension: String) {
            val s3Path = "${S3_DELIMITER}${ROOT_S3}/${userId}/${fileNameWithExtension}"
            this.path = s3Path
        }

        @Throws(IllegalArgumentException::class)
        private fun extensionIndex(fileNameWithExtension: String): Int {
            val index = fileNameWithExtension.lastIndexOf(EXTENSION_DELIMITER)
            return if (index == -1) throw IllegalArgumentException("FileName with extension must contain at least one [$EXTENSION_DELIMITER]") else index
        }
    }

}