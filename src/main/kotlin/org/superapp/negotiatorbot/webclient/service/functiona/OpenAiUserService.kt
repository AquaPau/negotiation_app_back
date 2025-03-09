package org.superapp.negotiatorbot.webclient.service.functiona

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.content.*
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import java.io.InputStream

private val log = KotlinLogging.logger {}

/**
 * User logic umbrella functions for access Open API capabilities
 */

interface OpenAiUserService {
    @Throws(NoSuchElementException::class)
    fun startDialogWIthUserPrompt(openAiAssistant: OpenAiAssistant, prompt: String): String
    fun uploadFile(openAiAssistant: OpenAiAssistant, fileContent: InputStream, fileName: String)
    fun uploadFilesAndExtractCompanyData(
        openAiAssistant: OpenAiAssistant,
        documents: List<RawDocumentAndMetatype>,
        prompt: String
    ): String

    fun deleteFilesFromOpenAi(openAiAssistant: OpenAiAssistant)

    fun updateThread(openAiAssistant: OpenAiAssistant)
}

@Service
class OpenAiUserServiceImpl(
    val openAiAssistantService: OpenAiAssistantService,
) : OpenAiUserService {

    @OptIn(BetaOpenAI::class)
    @Throws(NoSuchElementException::class)
    override fun startDialogWIthUserPrompt(openAiAssistant: OpenAiAssistant, prompt: String): String {
        val response = runBlocking { openAiAssistantService.runRequest(prompt, openAiAssistant) }

        return if (response.isEmpty()) {
            "try again please"
        } else {
            formResponse(response.first())
        }
    }

    override fun uploadFile(openAiAssistant: OpenAiAssistant, fileContent: InputStream, fileName: String) {
        openAiAssistantService.uploadFile(openAiAssistant, fileContent, fileName)
        log.info("Successfully uploaded file $fileName")
    }

    override fun uploadFilesAndExtractCompanyData(
        openAiAssistant: OpenAiAssistant,
        documents: List<RawDocumentAndMetatype>,
        prompt: String
    ): String {
        documents.forEach {
            openAiAssistantService.uploadFile(
                openAiAssistant,
                it.fileContent!!.inputStream(),
                it.fileNameWithExtensions
            )
        }
        return startDialogWIthUserPrompt(openAiAssistant, prompt)
    }

    override fun deleteFilesFromOpenAi(openAiAssistant: OpenAiAssistant) {
        openAiAssistantService.deleteVectorStoreFromAssistant(openAiAssistant)
        log.info("Successfully deleted all filer for $openAiAssistant")
    }

    override fun updateThread(openAiAssistant: OpenAiAssistant) {
        openAiAssistantService.updateThread(openAiAssistant)
    }

    @OptIn(BetaOpenAI::class)
    private fun formResponse(message: Message): String {
        val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
        return textContent.text.value
    }

}