package org.superapp.negotiatorbot.webclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.superapp.negotiatorbot.webclient.entity.PromptText
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.LegalType
import org.superapp.negotiatorbot.webclient.enums.PromptType
import java.util.*

@Repository
interface PromptTextRepository : JpaRepository<PromptText, Long> {

    fun findByAuditoryAndTypeAndPromptType(
        auditory: LegalType,
        type: DocumentType,
        promptType: PromptType
    ): Optional<PromptText>

    fun findByAuditoryAndPromptType(
        auditory: LegalType,
        promptType: PromptType
    ): Optional<PromptText>
}