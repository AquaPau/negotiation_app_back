package org.superapp.negotiatorbot.webclient.service.functionality

import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant

interface AssistantService<T> {
    fun getAssistant(relatedId: Long): OpenAiAssistant
}