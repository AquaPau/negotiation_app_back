package org.superapp.negotiatorbot.botclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.superapp.negotiatorbot.botclient.model.TgUser

@Repository
interface TgUserRepository : JpaRepository<TgUser, Long> {
    fun findByTgId(tgUserId: Long): TgUser?
}