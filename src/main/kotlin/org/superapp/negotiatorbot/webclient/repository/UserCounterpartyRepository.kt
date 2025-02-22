package org.superapp.negotiatorbot.webclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.entity.UserCounterparty
import java.util.*

interface UserCounterpartyRepository: JpaRepository<UserCounterparty, Long> {

    fun findAllByCompanyIdAndUser(companyId: Long, user: User): List<UserCounterparty>

    fun findByIdAndCompanyIdAndUser(id: Long, companyId: Long, user: User): Optional<UserCounterparty>
    fun findAllByCompanyId(companyId: Long): List<UserCounterparty>
}