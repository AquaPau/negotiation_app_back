package org.superapp.negotiatorbot.webclient.service.functiona

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.content.*
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.document.DocumentDataWithName
import org.superapp.negotiatorbot.webclient.entity.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.service.user.UserService
import java.io.InputStream

private val log = KotlinLogging.logger {}

/**
 * User logic umbrella functions for access Open API capabilities
 */
interface OpenAiUserService {
    @Throws(NoSuchElementException::class)
    fun startDialogWIthUserPrompt(userId: Long, prompt: String): String?
    suspend fun uploadFiles(userId: Long, fileContent: InputStream, fileName: String)
    suspend fun uploadFilesAndExtractCompanyData(
        userId: Long,
        documents: List<DocumentDataWithName>,
        prompt: String
    ): String

    suspend fun deleteFile(userId: Long)
}

@Service
class OpenAiUserServiceImpl(
    val openAiAssistantService: OpenAiAssistantService,
    val userService: UserService
) : OpenAiUserService {

    @OptIn(BetaOpenAI::class)
    @Throws(NoSuchElementException::class)
    override fun startDialogWIthUserPrompt(userId: Long, prompt: String): String {
        val openAiAssistant = getAssistant(userId)
        val response = runBlocking { openAiAssistantService.runRequest(prompt, openAiAssistant) }
        return formResponse(response.first())

    }

    @OptIn(BetaOpenAI::class)
    override suspend fun uploadFiles(userId: Long, fileContent: InputStream, fileName: String) {
        val assistantId = getAssistant(userId).getAssistantId()
        return openAiAssistantService.uploadFile(assistantId, fileContent, fileName)
    }

    @OptIn(BetaOpenAI::class)
    override suspend fun uploadFilesAndExtractCompanyData(
        userId: Long,
        documents: List<DocumentDataWithName>,
        prompt: String
    ): String {
        val assistantId = getAssistant(userId).getAssistantId()
        documents.forEach { openAiAssistantService.uploadFile(assistantId, it.fileContent, it.fileName) }
        return startDialogWIthUserPrompt(userId, prompt)
    }

    override suspend fun deleteFile(userId: Long) {
        val assistant = getAssistant(userId)
        openAiAssistantService.deleteVectorStoreFromAssistant(assistant)
        openAiAssistantService.deleteFile(assistant.fileId)
    }

    @OptIn(BetaOpenAI::class)
    private fun formResponse(message: Message): String {
        val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
        return textContent.text.value
    }

    @Throws(NoSuchElementException::class)
    private fun getAssistant(userId: Long): OpenAiAssistant {
        val user: User = userService.findById(userId)
            ?: throw NoSuchElementException("User with ID $userId does not exists.")
        return openAiAssistantService.getAssistant(user)
    }

}