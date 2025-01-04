package org.superapp.negotiatorbot.webclient.dto.document

import java.util.zip.Checksum

data class DocumentEnrichedDto(
    val id: Int,
    val checksum: Checksum
) {
    lateinit var data: ByteArray
}
