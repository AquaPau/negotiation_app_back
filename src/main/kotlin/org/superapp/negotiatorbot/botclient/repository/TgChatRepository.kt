package org.superapp.negotiatorbot.botclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.superapp.negotiatorbot.botclient.model.TgChat

interface TgChatRepository : JpaRepository<TgChat, Long> {
    fun findByTgUserId(tgUserId: Long): TgChat?
}