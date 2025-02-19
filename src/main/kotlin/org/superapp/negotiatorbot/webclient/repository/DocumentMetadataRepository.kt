package org.superapp.negotiatorbot.webclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata

@Repository
interface DocumentMetadataRepository : JpaRepository<DocumentMetadata, Long> {

    fun findAllByBusinessTypeAndCompanyIdAndUserId(businessType: BusinessType, companyId: Long, userId: Long): List<DocumentMetadata>

    fun findAllByBusinessTypeAndCounterPartyId(businessType: BusinessType, counterPartyId: Long): List<DocumentMetadata>

    fun existsByUserIdAndName(userId: Long, name: String): Boolean
}