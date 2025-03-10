package org.superapp.negotiatorbot.webclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.superapp.negotiatorbot.webclient.entity.PromptText
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import org.superapp.negotiatorbot.webclient.enum.LegalType
import org.superapp.negotiatorbot.webclient.enum.PromptType
import java.util.*

@Repository
interface PromptTextRepository : JpaRepository<PromptText, Long> {

    fun findByAuditoryAndTypeAndPromptType(
        auditory: LegalType,
        type: DocumentType,
        promptType: PromptType
    ): Optional<PromptText>
}