package org.superapp.negotiatorbot.webclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.superapp.negotiatorbot.webclient.entity.PromptText

interface PromptTextRepository: JpaRepository<PromptText, Long>