package org.superapp.negotiatorbot.webclient.repository.company

import org.springframework.data.jpa.repository.JpaRepository
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.entity.UserCompany
import java.util.*

interface UserCompanyRepository : JpaRepository<UserCompany, Long> {

    fun findByOgrn(ogrn: String): Optional<UserCompany>
    fun findAllByUserOrderByIdAsc(user: User): List<UserCompany>

    fun findByUserAndId(user: User, id: Long): Optional<UserCompany>
}