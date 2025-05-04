package org.superapp.negotiatorbot.webclient.service.util

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.exception.DocumentNotValidException
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentFactory.Companion.EXTENSION_DELIMITER

const val MAX_ALLOWED_SIZE_IN_MB = 10L
const val MAX_ALLOWED_FILES = 5


@Service
class MultipartFileValidator {

    companion object {
        private val supportedExtensions = setOf("pdf", "docx", "txt", "doc")

        fun validate(files: List<MultipartFile>, fileNamesWithExtensions: List<String>) {
            validateNumberOfFiles(files)
            files.mapIndexed { index, file -> validate(file, fileNamesWithExtensions[index]) }
        }

        fun validate(file: MultipartFile, fileNameWithExtension: String) {
            validateSize(file, fileNameWithExtension)
            validateExtension(fileNameWithExtension)
        }

        private fun validateNumberOfFiles(files: List<MultipartFile>) {
            if (files.size > MAX_ALLOWED_FILES) {
                throw DocumentNotValidException("Превышено допустимое количество файлов, максимум: $MAX_ALLOWED_FILES")
            }
        }

        private fun validateSize(file: MultipartFile, fileNameWithExtension: String) {
            val maxSizeInBytes = MAX_ALLOWED_SIZE_IN_MB * 1024 * 1024
            if (file.size > maxSizeInBytes) {
                throw DocumentNotValidException(
                    "Максимальный допустимый размер файла $maxSizeInBytes, " +
                            "однако для $fileNameWithExtension он составляет ${file.size}"
                )
            }
        }

        private fun validateExtension(fileNameWithExtension: String) {
            val extension = fileNameWithExtension.substringAfterLast(EXTENSION_DELIMITER)
            if (extension.equals(fileNameWithExtension, ignoreCase = true)) {
                throw DocumentNotValidException(
                    "Недопустимо загружать файл " +
                            "$fileNameWithExtension с расширением $extension"
                )
            }
            if (!supportedExtensions.contains(extension)) {
                throw DocumentNotValidException(
                    "Расширение файла [$extension] " +
                            "не поддерживается, допустимые: $supportedExtensions"
                )
            }
        }

    }

}