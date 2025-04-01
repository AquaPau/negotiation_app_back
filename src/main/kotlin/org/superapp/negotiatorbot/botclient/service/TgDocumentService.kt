package org.superapp.negotiatorbot.botclient.service

import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.exception.ChatNotFound
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.repository.TgDocumentRepository
import org.telegram.telegrambots.meta.api.objects.message.Message

interface TgDocumentService {
    fun create(message: Message, tgUserDbId: Long): TgDocument
    fun findByChatId(chatId: Long): TgDocument
}

@Service
class TgDocumentServiceImpl(
    private val tgDocumentRepository: TgDocumentRepository
) : TgDocumentService {
    override fun create(message: Message, tgUserDbId: Long): TgDocument {
        val document = message.document!!
        val tgDocument = TgDocument(
            messageId = message.messageId,
            chatId = message.chat.id,
            tgUserDbId = tgUserDbId,
            tgFileId = document.fileId,
            tgDocumentName = document.fileName
        )
        return tgDocumentRepository.save(tgDocument)
    }

    override fun findByChatId(chatId: Long): TgDocument {
        return tgDocumentRepository.findByChatId(chatId) ?: throw ChatNotFound("Dint found documentChatId")
    }
}