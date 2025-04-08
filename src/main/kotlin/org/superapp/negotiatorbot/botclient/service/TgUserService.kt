package org.superapp.negotiatorbot.botclient.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.superapp.negotiatorbot.botclient.model.TgUser
import org.superapp.negotiatorbot.botclient.repository.TgUserRepository
import org.telegram.telegrambots.meta.api.objects.User

private val log = KotlinLogging.logger {}

@Transactional
interface TgUserService {
    fun getTgUser(externalTgUser: User): TgUser
}

@Service
class TgUserServiceImpl(private val tgUserRepository: TgUserRepository) : TgUserService {
    override fun getTgUser(externalTgUser: User) =
        tgUserRepository.findByTgId(externalTgUser.id) ?: create(externalTgUser)

    private fun create(user: User): TgUser {
        val newUser = TgUser(
            tgId = user.id,
            tgUsername = user.userName,
        )
        val savedUser = tgUserRepository.save(newUser)
        log.info("Saved new user: $savedUser")
        return savedUser
    }
}