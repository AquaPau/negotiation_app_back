package org.superapp.negotiatorbot.webclient.service.functiona

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.file.FileId
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistantFileStorage
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiFile
import org.superapp.negotiatorbot.webclient.port.OpenAiAssistantPort
import org.superapp.negotiatorbot.webclient.repository.assistant.OpenAiAssistantFileStorageRepository
import org.superapp.negotiatorbot.webclient.repository.assistant.OpenAiFileRepository

@Transactional
interface OpenAiAssistantFileStorageService {
    fun getOrCreate(openAiAssistant: OpenAiAssistant): OpenAiAssistantFileStorage
    fun addFile(openAiAssistantFileStorage: OpenAiAssistantFileStorage, fileId: FileId)
    fun deleteVectorStore(openAiAssistantFileStorage: OpenAiAssistantFileStorage)
}

@Service
class OpenAiAssistantFileStorageServiceImpl(
    val openAiAssistantFileStorageRepository: OpenAiAssistantFileStorageRepository,
    val openAiAssistantPort: OpenAiAssistantPort,
    val openAiFileRepository: OpenAiFileRepository
) : OpenAiAssistantFileStorageService {

    @OptIn(BetaOpenAI::class)
    override fun getOrCreate(openAiAssistant: OpenAiAssistant): OpenAiAssistantFileStorage {
        val openAiVectorStore = openAiAssistant.openAiAssistantFileStorage
        if (openAiVectorStore == null) {
            val vectorId = create(openAiAssistant.getAssistantId())
            val vectorStore = OpenAiAssistantFileStorage()

            vectorStore.openAiVectorStoreId = vectorId
            vectorStore.openAiAssistant = openAiAssistant
            runBlocking {
                openAiAssistantPort.updateAssistant(
                    openAiAssistant.getAssistantId(),
                    vectorStore.getVectorStoreId()
                )
            }

            return openAiAssistantFileStorageRepository.save(vectorStore)
        }
        return openAiVectorStore
    }

    override fun deleteVectorStore(openAiAssistantFileStorage: OpenAiAssistantFileStorage) {
        openAiAssistantFileStorage.openAiFiles.forEach { deleteFile(it) }
        openAiAssistantPort.deleteVectorStore(openAiAssistantFileStorage.getVectorStoreId())
        openAiAssistantFileStorageRepository.delete(openAiAssistantFileStorage)
    }

    override fun addFile(openAiAssistantFileStorage: OpenAiAssistantFileStorage, fileId: FileId) {
        val file = OpenAiFile()
        file.fileId = fileId.id
        file.openAiAssistantFileStorage = openAiAssistantFileStorage
        openAiAssistantFileStorage.openAiFiles.add(file)
        openAiAssistantPort.updateVectorStore(openAiAssistantFileStorage.getVectorStoreId(), listOf(fileId))
        openAiFileRepository.save(file)
    }


    @OptIn(BetaOpenAI::class)
    private fun create(assistantId: AssistantId): String {
        return openAiAssistantPort.createVectorStore(assistantId).id.id
    }

    private fun deleteFile(openAiFile: OpenAiFile) {
        openAiAssistantPort.deleteOpenAiFile(openAiFile.getFileId())
        openAiFileRepository.delete(openAiFile)
    }
}