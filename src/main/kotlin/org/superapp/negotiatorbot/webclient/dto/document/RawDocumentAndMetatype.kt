package org.superapp.negotiatorbot.webclient.dto.document

import org.superapp.negotiatorbot.webclient.enum.DocumentType

data class RawDocumentAndMetatype(
    val documentType: DocumentType,
    val fileContent: ByteArray,
    val fileNameWithExtensions: String,
)
