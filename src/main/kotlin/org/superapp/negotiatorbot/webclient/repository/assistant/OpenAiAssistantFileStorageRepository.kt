package org.superapp.negotiatorbot.webclient.repository.assistant

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistantFileStorage

@Repository
interface OpenAiAssistantFileStorageRepository : JpaRepository<OpenAiAssistantFileStorage, Long> {
}