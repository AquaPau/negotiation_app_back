package org.superapp.negotiatorbot.botclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.superapp.negotiatorbot.botclient.model.TgDocument

interface TgChatRepository : JpaRepository<TgDocument, Long> {
    fun findByTgUserId(tgUserId: Long): TgDocument?
}