package org.superapp.negotiatorbot.botclient.dto

import org.superapp.negotiatorbot.webclient.enums.PromptType

data class ChosenPromptOption(val tgDocumentId: Long, val promptType: PromptType)
