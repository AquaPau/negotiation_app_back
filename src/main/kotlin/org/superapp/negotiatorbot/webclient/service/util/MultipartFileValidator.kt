package org.superapp.negotiatorbot.webclient.service.util

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.service.serversidefile.DocumentHelper.Companion.EXTENSION_DELIMITER

const val MAX_ALLOWED_SIZE_IN_MB = 10L
const val MAX_ALLOWED_FILES = 5


@Service
class MultipartFileValidator {

    companion object {
        private val supportedExtensions = setOf("pdf", "docx", "txt", "doc")

        @Throws(IllegalArgumentException::class)
        fun validate(files: List<MultipartFile>, fileNamesWithExtensions: List<String>) {
            validateNumberOfFiles(files)
            files.mapIndexed { index, file -> validate(file, fileNamesWithExtensions[index]) }
        }

        @Throws(IllegalArgumentException::class)
        fun validate(file: MultipartFile, fileNameWithExtension: String) {
            validateSize(file, fileNameWithExtension)
            validateExtension(fileNameWithExtension)
        }

        private fun validateNumberOfFiles(files: List<MultipartFile>) {
            if (files.size > MAX_ALLOWED_FILES) {
                throw IllegalArgumentException("Max number of files is $MAX_ALLOWED_FILES")
            }
        }

        private fun validateSize(file: MultipartFile, fileNameWithExtension: String) {
            val maxSizeInBytes = MAX_ALLOWED_SIZE_IN_MB * 1024 * 1024
            if (file.size > maxSizeInBytes) {
                throw IllegalArgumentException("Max allowed size is $maxSizeInBytes, but for file $fileNameWithExtension is ${file.size}")
            }
        }

        private fun validateExtension(fileNameWithExtension: String) {
            val extension = fileNameWithExtension.substringAfterLast(EXTENSION_DELIMITER)
            if (extension.equals(fileNameWithExtension, ignoreCase = true)) {
                throw IllegalArgumentException("File $fileNameWithExtension does not match the extension $extension")
            }
            if (!supportedExtensions.contains(extension)) {
                throw IllegalArgumentException("Invalid extension [$extension], supported: $supportedExtensions")
            }
        }

    }

}