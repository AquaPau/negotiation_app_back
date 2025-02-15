package org.superapp.negotiatorbot.webclient.port

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.*
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.file.*
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.Run
import com.aallam.openai.api.run.RunId
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.api.thread.ThreadRequest
import com.aallam.openai.api.vectorstore.FileBatchRequest
import com.aallam.openai.api.vectorstore.VectorStore
import com.aallam.openai.api.vectorstore.VectorStoreId
import com.aallam.openai.api.vectorstore.VectorStoreRequest
import com.aallam.openai.client.OpenAI
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.config.OpenAiAssistantConfig
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.entity.User


interface OpenAiAssistantPort {

    fun createAssistant(user: User): OpenAiAssistant

    @OptIn(BetaOpenAI::class)
    fun updateAssistant(assistantId: AssistantId, vectorId: VectorStoreId): Assistant

    @OptIn(BetaOpenAI::class)
    fun createVectorStore(assistantId: AssistantId): VectorStore
    fun deleteVectorStore(vectorStoreId: VectorStoreId)
    fun updateVectorStore(vectorId: VectorStoreId, files: List<FileId>)

    fun uploadOpenAiFile(fileSource: FileSource): File
    fun deleteOpenAiFile(fileId: FileId)

    @OptIn(BetaOpenAI::class)
    suspend fun createMessage(threadId: ThreadId, prompt: String): Message

    @OptIn(BetaOpenAI::class)
    suspend fun createRun(threadId: ThreadId, assistantId: AssistantId): Run

    @OptIn(BetaOpenAI::class)
    suspend fun processRequestRun(threadId: ThreadId, runId: RunId): List<Message>


}

@Service
class OpenAiAssistantPortImpl(
    private val openAI: OpenAI,
    private val openAiAssistantConfig: OpenAiAssistantConfig
) : OpenAiAssistantPort {

    private val log = KotlinLogging.logger {}

    @OptIn(BetaOpenAI::class)
    override fun createAssistant(user: User): OpenAiAssistant {

        val assistant = runBlocking {
            openAI.assistant(
                request = AssistantRequest(
                    name = "For userid: ${user.id}",
                    instructions = openAiAssistantConfig.instructions,
                    model = ModelId(openAiAssistantConfig.model),
                )
            )
        }
        val thread = runBlocking { getThread(user) }
        return createAssistant(assistant, thread, user)
    }


    @OptIn(BetaOpenAI::class)
    override fun updateAssistant(assistantId: AssistantId, vectorId: VectorStoreId): Assistant {
        return runBlocking {
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
        }
    }


    @OptIn(BetaOpenAI::class)
    override fun createVectorStore(assistantId: AssistantId): VectorStore {
        return runBlocking {
            openAI.createVectorStore(
                request = VectorStoreRequest(
                    name = "Store for assistant id: $assistantId"
                )
            )
        }
    }


    @OptIn(BetaOpenAI::class)
    override fun updateVectorStore(vectorId: VectorStoreId, files: List<FileId>) {
        runBlocking {
            openAI.createVectorStoreFilesBatch(
                id = vectorId,
                request = FileBatchRequest(
                    fileIds = files
                )
            )
        }
    }

    @OptIn(BetaOpenAI::class)
    override fun deleteVectorStore(vectorStoreId: VectorStoreId) {
        runBlocking { openAI.delete(id = vectorStoreId) }
    }

    override fun uploadOpenAiFile(fileSource: FileSource): File {
        return runBlocking {
            openAI.file(
                request = FileUpload(
                    file = fileSource,
                    purpose = Purpose("assistants")
                )
            )
        }
    }

    override fun deleteOpenAiFile(fileId: FileId) {
        runBlocking { openAI.delete(fileId) }
    }

    @OptIn(BetaOpenAI::class)
    override suspend fun createMessage(threadId: ThreadId, prompt: String): Message {
        return openAI.message(
            threadId = threadId,
            request = MessageRequest(
                role = Role.User,
                content = prompt,
            )
        )
    }

    @OptIn(BetaOpenAI::class)
    override suspend fun createRun(threadId: ThreadId, assistantId: AssistantId): Run {
        return openAI.createRun(
            threadId,
            request = RunRequest(
                assistantId = assistantId,
            )
        )
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
        return openAiAssistant
    }

    @OptIn(BetaOpenAI::class)
    override suspend fun processRequestRun(threadId: ThreadId, runId: RunId): List<Message> {
        log.debug("Started run for thread [${threadId}]")
        do {
            val retrievedRun = openAI.getRun(threadId = threadId, runId = runId)
            delay(1000)
        } while (retrievedRun.status != Status.Completed)
        log.debug("Finished run for thread [${threadId}]")
        return openAI.messages(threadId)
    }
}