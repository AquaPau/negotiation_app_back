package org.superapp.negotiatorbot.botclient.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.model.TgUser
import org.superapp.negotiatorbot.botclient.repository.TgUserRepository
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import org.superapp.negotiatorbot.webclient.enum.PromptType
import org.telegram.telegrambots.meta.api.objects.User

private val log = KotlinLogging.logger {}

interface TgUserService {
    fun create(user: User): TgUser
    fun findByTgId(tgUserId: Long): TgUser?

    fun updateDocumentType(tgUser: TgUser, documentType: DocumentType): TgUser
    fun updatePromptType(tgUser: TgUser, promptType: PromptType): TgUser

    fun clearTypes(tgUser: TgUser): TgUser

    fun getTypes(tgUser: TgUser): Pair<DocumentType, PromptType>?
}

@Service
class TgUserServiceImpl(private val tgUserRepository: TgUserRepository) : TgUserService {
    override fun findByTgId(tgUserId: Long) = tgUserRepository.findByTgId(tgUserId)

    override fun updateDocumentType(tgUser: TgUser, documentType: DocumentType): TgUser {
        tgUser.chosenDocumentType = documentType
        return tgUserRepository.save(tgUser)
    }

    override fun updatePromptType(tgUser: TgUser, promptType: PromptType): TgUser {
        tgUser.chosenPromptType = promptType
        return tgUserRepository.save(tgUser)
    }

    override fun clearTypes(tgUser: TgUser): TgUser {
        tgUser.chosenDocumentType = null
        tgUser.chosenPromptType = null
        return tgUserRepository.save(tgUser)
    }

    override fun create(user: User): TgUser {
        val newUser = TgUser(
            tgId = user.id,
            tgUsername = user.userName,
        )
        val savedUser = tgUserRepository.save(newUser)
        log.info("Saved new user: $savedUser")
        return savedUser
    }

    override fun getTypes(tgUser: TgUser): Pair<DocumentType, PromptType>? {
        val docType = tgUser.chosenDocumentType
        val promptType = tgUser.chosenPromptType
        return if (docType != null && promptType != null) {
            return docType to promptType
        } else {
            null
        }
    }
}