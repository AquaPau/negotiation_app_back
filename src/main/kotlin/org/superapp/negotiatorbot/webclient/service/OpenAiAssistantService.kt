package org.superapp.negotiatorbot.webclient.service

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.*
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadRequest
import com.aallam.openai.api.vectorstore.VectorStoreId
import com.aallam.openai.api.vectorstore.VectorStoreRequest
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.config.OpenAiAssistantConfig
import org.superapp.negotiatorbot.webclient.entity.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.repository.OpenAiAssistantRepository

interface OpenAiAssistantService {
    fun getAssistant(user: User): OpenAiAssistant

    @OptIn(BetaOpenAI::class)
    suspend fun connectFileToAssistant(assistantId: AssistantId, fileId: FileId)
    suspend fun deleteVectorStoreFromAssistant(assistant: OpenAiAssistant)
}

@Service
class OpenAiAssistantServiceImpl(
    val openAI: OpenAI,
    val openAiAssistantConfig: OpenAiAssistantConfig,
    val openAiAssistantRepository: OpenAiAssistantRepository
) : OpenAiAssistantService {

    override fun getAssistant(user: User): OpenAiAssistant {
        return openAiAssistantRepository.findByUser(user) ?: runBlocking {
            createAssistant(user)
        }
    }

    @OptIn(BetaOpenAI::class)
    override suspend fun connectFileToAssistant(assistantId: AssistantId, fileId: FileId) {

        val vectorId = openAI.createVectorStore(
            request = VectorStoreRequest(
                name = "Store for assistant id: $assistantId",
                fileIds = listOf(fileId)
            )
        ).id

        openAI.assistant(
            id = assistantId,
            request = AssistantRequest(
                tools = listOf(AssistantTool.FileSearch),
                toolResources = ToolResources(
                    fileSearch = FileSearchResources(
                        vectorStoreIds = listOf(vectorId)
                    ),
                )
            )
        )

        val toUpdate = openAiAssistantRepository.findByAssistantId(assistantId.id)
            ?: throw IllegalStateException("Cannot find assistant for id: $assistantId")
        toUpdate.vectorStoreId = vectorId.id
        toUpdate.fileId = fileId.id
        openAiAssistantRepository.save(toUpdate)
    }

    override suspend fun deleteVectorStoreFromAssistant(assistant: OpenAiAssistant) {
        assistant.vectorStoreId?.let {
            deleteVectorStore(VectorStoreId(it))
        }
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun deleteVectorStore(vectorStoreId: VectorStoreId) {
        openAI.delete(id = vectorStoreId)
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun createAssistant(user: User): OpenAiAssistant {
        val assistant = openAI.assistant(
            request = AssistantRequest(
                name = "For userid: ${user.id}",
                instructions = openAiAssistantConfig.instructions,
                model = ModelId(openAiAssistantConfig.model),
            )
        )
        val thread = getThread(user)
        return createAssistant(assistant, thread, user)
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun getThread(user: User) = openAI.thread(
        request = ThreadRequest(
            metadata = mapOf(
                "user" to "${user.id}"
            )
        )
    )

    @OptIn(BetaOpenAI::class)
    private fun createAssistant(assistant: Assistant, thread: Thread, user: User): OpenAiAssistant {
        val openAiAssistant = OpenAiAssistant()
        openAiAssistant.user = user
        openAiAssistant.threadId = thread.id.id
        openAiAssistant.assistantId = assistant.id.id
        return openAiAssistantRepository.save(openAiAssistant)
    }
}