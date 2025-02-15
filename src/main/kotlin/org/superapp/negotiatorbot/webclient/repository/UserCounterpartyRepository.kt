package org.superapp.negotiatorbot.webclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.entity.UserCounterparty

interface UserCounterpartyRepository: JpaRepository<UserCounterparty, Long> {

    fun findAllByUser(user: User): List<UserCounterparty>
}