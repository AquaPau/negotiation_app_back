package org.superapp.negotiatorbot.botclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.superapp.negotiatorbot.botclient.model.TgDocument

interface TgDocumentRepository : JpaRepository<TgDocument, Long> {
    fun findByChosenDocumentTypeNotNullAndChosenPromptTypeNotNullAndMessageIdIsNull(): List<TgDocument>
}