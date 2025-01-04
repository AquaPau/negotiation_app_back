package org.superapp.negotiatorbot.webclient.dto.user

import org.superapp.negotiatorbot.webclient.dto.document.DocumentDto

data class UserHistoryDto(
    val userId: Int,
    val documentList: List<DocumentDto>
)
