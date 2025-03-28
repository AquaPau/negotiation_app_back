package org.superapp.negotiatorbot.webclient.dto.document

import org.superapp.negotiatorbot.webclient.enums.DocumentType

data class RawDocumentAndMetatype(
    val documentType: DocumentType,
    val fileNameWithExtensions: String,
) {
    var fileContent: ByteArray? = null
}