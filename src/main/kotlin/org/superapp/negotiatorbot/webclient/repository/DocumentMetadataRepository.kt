package org.superapp.negotiatorbot.webclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.User

@Repository
interface DocumentMetadataRepository : JpaRepository<DocumentMetadata, Long> {

    fun findAllByBusinessTypeAndCounterPartyId(businessType: BusinessType, counterPartyId: Long): List<DocumentMetadata>

    fun existsByUserAndName(user: User, name: String): Boolean
}