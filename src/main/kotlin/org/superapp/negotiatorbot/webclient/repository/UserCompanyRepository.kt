package org.superapp.negotiatorbot.webclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.entity.UserCompany
import java.util.*

interface UserCompanyRepository: JpaRepository<UserCompany, Long> {

    fun findByUser(user: User): Optional<UserCompany>
}