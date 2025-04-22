package org.superapp.negotiatorbot.botclient.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.repository.TgDocumentRepository
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.telegram.telegrambots.meta.api.objects.Document
import org.telegram.telegrambots.meta.api.objects.chat.Chat

@Transactional
interface TgDocumentService {
    fun create(chat: Chat, tgUserDbId: Long): TgDocument
    fun getReadyToUploadDoc(chatId: Long): TgDocument?
    fun deleteUnfinishedDocuments(chatId: Long)
    fun addDocument(tgDocument: TgDocument, document: Document): TgDocument
    fun addReplyTo(tgDocument: TgDocument, messageId: Int): TgDocument
    fun updateDocumentType(idDb: Long, docType: DocumentType): TgDocument
    fun updatePromptType(idDb: Long, promptType: PromptType): TgDocument
}


@Service
class TgDocumentServiceImpl(
    private val tgDocumentRepository: TgDocumentRepository
) : TgDocumentService {
    override fun create(chat: Chat, tgUserDbId: Long): TgDocument {
        val tgDocument = TgDocument(
            chatId = chat.id,
            tgUserDbId = tgUserDbId,
        )
        return tgDocumentRepository.save(tgDocument)
    }

    override fun deleteUnfinishedDocuments(chatId: Long) {
        tgDocumentRepository.deleteByChatIdAndChosenPromptTypeIsNull(chatId)
    }

    override fun getReadyToUploadDoc(chatId: Long): TgDocument? {
        val readyDocs =
            tgDocumentRepository.findByChatIdAndChosenDocumentTypeNotNullAndChosenPromptTypeIsNull(chatId)
        if (readyDocs.isEmpty()) return null else return readyDocs.first()
    }

    override fun addDocument(tgDocument: TgDocument, document: Document): TgDocument {
        tgDocument.tgDocumentName = document.fileName
        tgDocument.tgFileId = document.fileId
        return tgDocumentRepository.save(tgDocument)
    }

    override fun addReplyTo(tgDocument: TgDocument, messageId: Int): TgDocument {
        tgDocument.messageId = messageId
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