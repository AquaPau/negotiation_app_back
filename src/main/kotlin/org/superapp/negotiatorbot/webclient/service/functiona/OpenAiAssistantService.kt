package org.superapp.negotiatorbot.webclient.service.functiona

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.api.vectorstore.VectorStoreId
import kotlinx.coroutines.runBlocking
import okio.source
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.port.OpenAiAssistantPort
import org.superapp.negotiatorbot.webclient.repository.OpenAiAssistantRepository
import java.io.InputStream

/**
 * Wrapped basic functions to the OpenAI API without user logic
 */
interface OpenAiAssistantService {
    fun getAssistant(user: User): OpenAiAssistant

    @OptIn(BetaOpenAI::class)
    suspend fun connectFileToAssistant(assistantId: AssistantId, fileId: FileId)
    suspend fun deleteVectorStoreFromAssistant(assistant: OpenAiAssistant)

    @OptIn(BetaOpenAI::class)
    suspend fun runRequest(prompt: String, assistant: OpenAiAssistant): List<Message>

    @OptIn(BetaOpenAI::class)
    suspend fun uploadFile(assistantId: AssistantId, fileContent: InputStream, fileName: String)

    suspend fun deleteFile(fileId: String?)
}

@Service
class OpenAiAssistantServiceImpl(
    val openAiAssistantRepository: OpenAiAssistantRepository,
    val openAiAssistantPort: OpenAiAssistantPort
) : OpenAiAssistantService {

    override fun getAssistant(user: User): OpenAiAssistant {
        return openAiAssistantRepository.findByUser(user) ?: runBlocking {
            val assistant = openAiAssistantPort.createAssistant(user)
            openAiAssistantRepository.save(assistant)
        }
    }

    @OptIn(BetaOpenAI::class)
    override suspend fun connectFileToAssistant(assistantId: AssistantId, fileId: FileId) {

        val vectorId = openAiAssistantPort.createVectorStore(assistantId, fileId).id

        openAiAssistantPort.updateAssistant(assistantId, vectorId)

        val assistantDataToUpdate = openAiAssistantRepository.findByAssistantId(assistantId.id)
            ?: throw IllegalStateException("Cannot find assistant for id: $assistantId")
        assistantDataToUpdate.vectorStoreId = vectorId.id
        assistantDataToUpdate.fileId = fileId.id
        openAiAssistantRepository.save(assistantDataToUpdate)
    }

    override suspend fun deleteVectorStoreFromAssistant(assistant: OpenAiAssistant) {
        assistant.vectorStoreId?.let {
            openAiAssistantPort.deleteVectorStore(VectorStoreId(it))
        }
    }

    @OptIn(BetaOpenAI::class)
    override suspend fun runRequest(prompt: String, assistant: OpenAiAssistant): List<Message> {
        val threadId = ThreadId(assistant.threadId!!)
        openAiAssistantPort.createMessage(threadId, prompt)

        val run = openAiAssistantPort.createRun(threadId, assistant.getAssistantId())

        return openAiAssistantPort.processRequestRun(threadId, run.id)
    }

    @OptIn(BetaOpenAI::class)
    override suspend fun uploadFile(assistantId: AssistantId, fileContent: InputStream, fileName: String) {
        val fileSource = FileSource(name = fileName, source = fileContent.source())
        val fileId = openAiAssistantPort.uploadOpenAiFile(fileSource).id
        connectFileToAssistant(assistantId, fileId)
    }

    override suspend fun deleteFile(fileId: String?) {
        fileId?.let { openAiAssistantPort.removeOpenAiFile(FileId(fileId)) }
    }
}