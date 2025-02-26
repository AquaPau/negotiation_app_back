package org.superapp.negotiatorbot.webclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata

@Repository
interface DocumentMetadataRepository : JpaRepository<DocumentMetadata, Long> {

    fun findAllByBusinessTypeAndCompanyIdAndUserIdOrderByIdAsc(
        businessType: BusinessType,
        companyId: Long,
        userId: Long
    ): List<DocumentMetadata>

    fun findAllByBusinessTypeAndContractorIdAndCompanyIdOrderByIdAsc(
        businessType: BusinessType,
        counterPartyId: Long,
        companyId: Long
    ): List<DocumentMetadata>

    fun findAllByContractorId(companyId: Long): List<DocumentMetadata>

    fun findAllByCompanyIdAndContractorIdIsNull(companyId: Long): List<DocumentMetadata>

    fun existsByUserIdAndName(userId: Long, name: String): Boolean
}