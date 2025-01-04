package org.superapp.negotiatorbot.webclient.dto.document

import java.util.zip.Checksum

data class DocumentDto(
    val id: Int,
    val checksum: Checksum
)
