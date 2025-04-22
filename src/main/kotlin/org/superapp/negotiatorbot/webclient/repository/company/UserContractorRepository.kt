package org.superapp.negotiatorbot.webclient.repository.company

import org.springframework.data.jpa.repository.JpaRepository
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.entity.UserContractor
import java.util.*

interface UserContractorRepository: JpaRepository<UserContractor, Long> {

    fun findAllByCompanyIdAndUserOrderByIdAsc(companyId: Long, user: User): List<UserContractor>

    fun findByIdAndCompanyIdAndUser(id: Long, companyId: Long, user: User): Optional<UserContractor>
    fun findAllByCompanyId(companyId: Long): List<UserContractor>
}