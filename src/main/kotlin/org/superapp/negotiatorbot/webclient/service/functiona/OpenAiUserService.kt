package org.superapp.negotiatorbot.webclient.service.functiona

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.content.*
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.User
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.service.user.UserService
import java.io.InputStream

private val log = KotlinLogging.logger {}

/**
 * User logic umbrella functions for access Open API capabilities
 */

@Transactional
interface OpenAiUserService {
    @Throws(NoSuchElementException::class)
    fun startDialogWIthUserPrompt(userId: Long, prompt: String): String?
    fun uploadFiles(userId: Long, fileContent: InputStream, fileName: String)
    fun uploadFilesAndExtractCompanyData(
        userId: Long,
        documents: List<RawDocumentAndMetatype>,
        prompt: String
    ): String

    fun deleteFilesFromOpenAi(userId: Long)
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

    override fun uploadFiles(userId: Long, fileContent: InputStream, fileName: String) {
        val assistant = getAssistant(userId)
        openAiAssistantService.uploadFile(assistant, fileContent, fileName)
        log.info("Successfully uploaded file $fileName")
    }

    override fun uploadFilesAndExtractCompanyData(
        userId: Long,
        documents: List<RawDocumentAndMetatype>,
        prompt: String
    ): String {
        val assistant = getAssistant(userId)
        documents.forEach {
            openAiAssistantService.uploadFile(
                assistant,
                it.fileContent.inputStream(),
                it.fileNameWithExtensions
            )
        }
        return startDialogWIthUserPrompt(userId, prompt)
    }

    override fun deleteFilesFromOpenAi(userId: Long) {
        val assistant = getAssistant(userId)
        openAiAssistantService.deleteVectorStoreFromAssistant(assistant)
        log.info("Successfully deleted all filer for $userId")
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