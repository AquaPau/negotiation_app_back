package org.superapp.negotiatorbot.webclient.dto.document

import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.enum.DocumentType

data class RawDocumentAndMetatype(
    val documentType: DocumentType,
    val rawFile: MultipartFile
)
