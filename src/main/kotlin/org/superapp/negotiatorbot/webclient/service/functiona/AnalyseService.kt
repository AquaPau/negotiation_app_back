package org.superapp.negotiatorbot.webclient.service.functiona

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.assistant.OpenAiAssistant
import org.superapp.negotiatorbot.webclient.promt.documentDescriptionPrompt
import org.superapp.negotiatorbot.webclient.promt.documentRisksPrompt
import org.superapp.negotiatorbot.webclient.service.company.CompanyService
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService
import org.superapp.negotiatorbot.webclient.service.s3.S3Service


interface AnalyseService {
    fun detectRisks(documentId: Long)

    suspend fun detectOpportunities(documentId: Long): String

    fun provideDescription(documentId: Long)

    fun updateThreadAndRun(documentId: Long, consumer: (Long) -> Unit)
}

@Service
class AnalyseServiceImpl(
    private val documentService: DocumentService,
    private val companyService: CompanyService,
    private val openAiUserService: OpenAiUserService,
    private val s3Service: S3Service
) : AnalyseService {

    @OptIn(DelicateCoroutinesApi::class)
    override fun detectRisks(documentId: Long) {
        val doc = documentService.get(documentId)
        doc.risks = "Риски документа загружаются"
        documentService.save(doc)
        GlobalScope.launch {
            doc.risks = provideResponseFromOpenAi(doc, documentRisksPrompt())
            documentService.save(doc)
        }
    }

    override suspend fun detectOpportunities(documentId: Long): String {
        TODO("Not yet implemented")
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun provideDescription(documentId: Long) {
        val doc = documentService.get(documentId)
        doc.description = "Содержимое документа загружается"
        documentService.save(doc)
        GlobalScope.launch {
            doc.description = provideResponseFromOpenAi(doc, documentDescriptionPrompt())
            documentService.save(doc)
        }

    }

    override fun updateThreadAndRun(documentId: Long, consumer: (Long) -> Unit) {
        val assistant = documentService.get(documentId).getAssistant()
        openAiUserService.updateThread(assistant)
        consumer.invoke(documentId)
    }

    private suspend fun provideResponseFromOpenAi(doc: DocumentMetadata, prompt: String): String {
        val fileContent = s3Service.download(doc.path!!)
        val openAiAssistant = doc.getAssistant()
        val fullDocName = doc.getNameWithExtension()

        openAiUserService.uploadFile(openAiAssistant, fileContent.inputStream, fullDocName)
        val result = openAiUserService.startDialogWIthUserPrompt(openAiAssistant, prompt)
        openAiUserService.deleteFilesFromOpenAi(openAiAssistant)
        return result.replace(
            regex = Regex("""【.*】"""),
            ""
        )
    }

    private fun DocumentMetadata.getAssistant() : OpenAiAssistant {
        val relatedId = this.relatedId!!
        return when (this.businessType) {
            BusinessType.USER -> companyService.getCompanyAssistant(relatedId)
            BusinessType.PARTNER -> companyService.getContractorAssistant(relatedId)
            else -> TODO("Not supported document business type")
        }
    }
}