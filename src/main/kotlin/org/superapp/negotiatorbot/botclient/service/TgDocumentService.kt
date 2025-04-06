package org.superapp.negotiatorbot.botclient.service

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.repository.TgDocumentRepository
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.telegram.telegrambots.meta.api.objects.Document
import org.telegram.telegrambots.meta.api.objects.message.Message

interface TgDocumentService {
    fun create(message: Message, tgUserDbId: Long): TgDocument
    fun getReadyToUploadDoc(): TgDocument?
    fun addDocument(tgDocument: TgDocument, document: Document): TgDocument
    fun addReplyTo(tgDocument: TgDocument, messageId: Int): TgDocument
    fun updateDocumentType(idDb: Long, docType: DocumentType): TgDocument
    fun updatePromptType(idDb: Long, promptType: PromptType): TgDocument
}

@Service
class TgDocumentServiceImpl(
    private val tgDocumentRepository: TgDocumentRepository
) : TgDocumentService {
    override fun create(message: Message, tgUserDbId: Long): TgDocument {
        val tgDocument = TgDocument(
            chatId = message.chat.id,
            tgUserDbId = tgUserDbId,
        )
        return tgDocumentRepository.save(tgDocument)
    }

    override fun getReadyToUploadDoc(): TgDocument? {
        val readyDocs =
            tgDocumentRepository.findByChosenDocumentTypeNotNullAndChosenPromptTypeNotNullAndMessageIdIsNull()
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