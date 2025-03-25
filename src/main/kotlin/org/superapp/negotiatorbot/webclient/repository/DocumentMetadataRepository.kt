package org.superapp.negotiatorbot.webclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import java.util.*

@Repository
interface DocumentMetadataRepository : JpaRepository<DocumentMetadata, Long> {

    fun findAllByBusinessTypeAndRelatedIdAndUserIdOrderByIdAsc(
        businessType: BusinessType,
        relatedId: Long,
        userId: Long
    ): List<DocumentMetadata>

    fun findAllByBusinessTypeAndRelatedId(businessType: BusinessType, relatedId: Long): List<DocumentMetadata>

    fun existsByUserIdAndName(userId: Long, name: String): Boolean

    fun findByIdAndBusinessTypeAndRelatedId(
        id: Long,
        businessType: BusinessType,
        relatedId: Long
    ): Optional<DocumentMetadata>
}