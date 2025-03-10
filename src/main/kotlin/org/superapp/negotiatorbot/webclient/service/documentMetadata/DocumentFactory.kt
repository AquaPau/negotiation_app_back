package org.superapp.negotiatorbot.webclient.service.documentMetadata

import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.enum.BusinessType
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import java.util.*


class DocumentFactory {


    companion object {
        private const val ROOT_S3 = "users"
        private const val S3_DELIMITER = "/"
        const val EXTENSION_DELIMITER = '.'

        private val supportedExtensions =
            setOf("application/pdf", "csv", "json", "docx", "doc", "txt", "md", "pdf", "tex", "xml", "html")

        fun createFile(
            userId: Long,
            businessType: BusinessType,
            relatedId: Long,
            documentType: DocumentType,
            fileNameWithExtension: String,
        ): DocumentMetadata {
            val newFile = DocumentMetadata()
            newFile.userId = userId
            newFile.businessType = businessType
            newFile.relatedId = relatedId
            newFile.documentType = documentType
            newFile.setNameAndExtension(fileNameWithExtension)
            newFile.defineAndSetS3Path()
            return newFile
        }


        fun createFiles(
            userId: Long,
            businessType: BusinessType,
            relatedId: Long,
            fileNameWithExtension: List<String>,
            documentTypes: List<DocumentType>
        ): List<DocumentMetadata> {
            return fileNameWithExtension.mapIndexed { index, it ->
                createFile(
                    userId = userId,
                    businessType = businessType,
                    documentType = documentTypes[index],
                    relatedId = relatedId,
                    fileNameWithExtension = fileNameWithExtension[index],
                )
            }
        }

        fun createFiles(
            userId: Long,
            businessType: BusinessType,
            companyId: Long,
            contractorId: Long,
            fileNameWithExtension: List<String>,
            documentTypes: List<DocumentType>
        ): List<DocumentMetadata> {
            return fileNameWithExtension.mapIndexed { index, it ->
                createFile(
                    userId = userId,
                    businessType = businessType,
                    documentType = documentTypes[index],
                    relatedId = contractorId,
                    fileNameWithExtension = fileNameWithExtension[index],
                )
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


        private fun DocumentMetadata.defineAndSetS3Path() {
            val s3Path =
                "${S3_DELIMITER}${ROOT_S3}/${userId!!}/${businessType!!}/${relatedId!!}/${UUID.randomUUID()}/${name!!}.${extension!!}"
            this.path = s3Path
        }

        @Throws(IllegalArgumentException::class)
        private fun extensionIndex(fileNameWithExtension: String): Int {
            val index = fileNameWithExtension.lastIndexOf(EXTENSION_DELIMITER)
            return if (index == -1) throw IllegalArgumentException("FileName with extension must contain at least one [$EXTENSION_DELIMITER]") else index
        }
    }

}