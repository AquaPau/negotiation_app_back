package org.superapp.negotiatorbot.webclient.dto.document

import java.io.InputStream

data class DocumentDataWithName(
    val fileContent: InputStream,
    val fileName: String
)
