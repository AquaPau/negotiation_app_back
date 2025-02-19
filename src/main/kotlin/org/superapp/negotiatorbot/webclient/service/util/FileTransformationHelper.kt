package org.superapp.negotiatorbot.webclient.service.util

import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.enum.DocumentType

class FileTransformationHelper {

    companion object {
        fun extractLoadedData(
            files: List<MultipartFile>,
            types: List<DocumentType>
        ): Pair<List<String>, List<RawDocumentAndMetatype>> {
            val fileNamesWithExtensions = files.map {
                it.originalFilename!!
            }
            val fileContents = files.mapIndexed { index, file ->
                RawDocumentAndMetatype(
                    types[index],
                    fileNamesWithExtensions[index]
                ).apply { fileContent = file.bytes }
            }
            return Pair(fileNamesWithExtensions, fileContents)
        }

    }
}