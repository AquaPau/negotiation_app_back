package org.superapp.negotiatorbot.webclient.repository.assistant

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.entity.User

@Repository
interface OpenAiAssistantRepository : JpaRepository<OpenAiAssistant, Long> {
    fun findByUser(user: User): OpenAiAssistant?
}