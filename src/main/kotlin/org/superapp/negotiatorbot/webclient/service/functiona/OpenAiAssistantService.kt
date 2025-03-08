package org.superapp.negotiatorbot.webclient.service.functiona

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.Assistant
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadId
import kotlinx.coroutines.runBlocking
import okio.source
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.port.OpenAiAssistantPort
import org.superapp.negotiatorbot.webclient.repository.assistant.OpenAiAssistantRepository
import java.io.InputStream

/**
 * Wrapped basic functions to the OpenAI API without user logic
 */
interface OpenAiAssistantService {

    fun getAssistant(assistantDBId: Long): OpenAiAssistant
    fun createAssistant(): OpenAiAssistant

    fun deleteVectorStoreFromAssistant(assistant: OpenAiAssistant)

    @OptIn(BetaOpenAI::class)
    suspend fun runRequest(prompt: String, assistant: OpenAiAssistant): List<Message>

    fun uploadFile(assistant: OpenAiAssistant, fileContent: InputStream, fileName: String)

    fun updateThread(assistant: OpenAiAssistant)
}

@Service
class OpenAiAssistantServiceImpl(
    val openAiAssistantRepository: OpenAiAssistantRepository,
    val openAiAssistantPort: OpenAiAssistantPort,
    val openAiOpenAiAssistantFileStorageService: OpenAiAssistantFileStorageService,
) : OpenAiAssistantService {

    @OptIn(BetaOpenAI::class)
    override fun getAssistant(assistantDBId: Long): OpenAiAssistant {
        val assistant = openAiAssistantRepository.findById(assistantDBId).orElseThrow()
        runBlocking {
            if (!openAiAssistantPort.existsInOpenAi(assistant)) {
                openAiAssistantPort.deleteThread(assistant.threadId)
                val pair = openAiAssistantPort.createAssistant()
                assistant.assistantId = pair.first.id.id
                assistant.threadId = pair.second.id.id
            }
        }
        return openAiAssistantRepository.save(assistant)
    }

    @OptIn(BetaOpenAI::class)
    override fun createAssistant(): OpenAiAssistant {
        val assistantWithThread = openAiAssistantPort.createAssistant()
        return openAiAssistantRepository.save(createAssistant(assistantWithThread.first, assistantWithThread.second))
    }

    @OptIn(BetaOpenAI::class)
    private fun createAssistant(assistant: Assistant, thread: Thread): OpenAiAssistant {
        val openAiAssistant = OpenAiAssistant()
        openAiAssistant.threadId = thread.id.id
        openAiAssistant.assistantId = assistant.id.id
        return openAiAssistant
    }

    override fun uploadFile(assistant: OpenAiAssistant, fileContent: InputStream, fileName: String) {
        val fileSource = FileSource(name = fileName, source = fileContent.source())
        val fileId = runBlocking { openAiAssistantPort.uploadOpenAiFile(fileSource).id }
        connectFileToAssistant(assistant, fileId)
    }

    override fun deleteVectorStoreFromAssistant(assistant: OpenAiAssistant) {
        assistant.openAiAssistantFileStorage?.let { store ->
            assistant.openAiAssistantFileStorage = null
            openAiOpenAiAssistantFileStorageService.deleteVectorStore(store)
            openAiAssistantRepository.save(assistant)
        }

    }

    private fun connectFileToAssistant(assistant: OpenAiAssistant, fileId: FileId) {
        val vectorStore = openAiOpenAiAssistantFileStorageService.getOrCreate(assistant)
        openAiOpenAiAssistantFileStorageService.addFile(vectorStore, fileId)
        assistant.openAiAssistantFileStorage = vectorStore
        openAiAssistantRepository.save(assistant)
    }

    @OptIn(BetaOpenAI::class)
    override suspend fun runRequest(prompt: String, assistant: OpenAiAssistant): List<Message> {
        val threadId = ThreadId(assistant.threadId!!)
        openAiAssistantPort.createMessage(threadId, prompt)

        val run = openAiAssistantPort.createRun(threadId, assistant.getAssistantId())

        val response = openAiAssistantPort.processRequestRun(threadId, run.id)

        if (response.isEmpty()) {
            updateThread(assistant)
        }
        return response
    }

    @OptIn(BetaOpenAI::class)
    override fun updateThread(assistant: OpenAiAssistant) {
        runBlocking {
            val newThread = openAiAssistantPort.refreshThread(assistant)
            assistant.threadId = newThread.id.id
            openAiAssistantRepository.save(assistant)
        }
    }
}