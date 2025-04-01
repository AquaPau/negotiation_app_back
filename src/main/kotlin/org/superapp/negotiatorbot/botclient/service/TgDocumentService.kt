package org.superapp.negotiatorbot.botclient.service

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.repository.TgDocumentRepository
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.telegram.telegrambots.meta.api.objects.message.Message

interface TgDocumentService {
    fun create(message: Message, tgUserDbId: Long): TgDocument
    fun updateDocumentType(idDb: Long, docType: DocumentType): TgDocument
    fun updatePromptType(idDb: Long, promptType: PromptType): TgDocument
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

    override fun updateDocumentType(idDb: Long, docType: DocumentType): TgDocument {
        val tgDoc = tgDocumentRepository.findById(idDb).orElseThrow()
        tgDoc.chosenDocumentType = docType
        return tgDocumentRepository.save(tgDoc)
    }

    override fun updatePromptType(idDb: Long, promptType: PromptType): TgDocument {
        val tgDoc = tgDocumentRepository.findById(idDb).orElseThrow()
        tgDoc.chosenPromptType = promptType
        return tgDocumentRepository.save(tgDoc)
    }
}