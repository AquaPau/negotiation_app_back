package org.superapp.negotiatorbot.webclient.service.functiona

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.result.BeneficiaryDto
import org.superapp.negotiatorbot.webclient.dto.result.CompanyOpportunitiesDto
import org.superapp.negotiatorbot.webclient.dto.result.CompanyRisksDto
import org.superapp.negotiatorbot.webclient.promt.documentDescriptionPrompt
import org.superapp.negotiatorbot.webclient.service.s3.S3Service
import org.superapp.negotiatorbot.webclient.service.serversidefile.DocumentService


interface AnalyseService {

    fun detectBeneficiary(): BeneficiaryDto

    fun detectRisks(): CompanyRisksDto

    fun detectOpportunities(): CompanyOpportunitiesDto

    fun provideDescription(documentId: Long): String
}

@Service
class AnalyseServiceImpl(
    private val documentService: DocumentService,
    private val userOpenAiUserService: OpenAiUserService,
    private val s3Service: S3Service
) : AnalyseService {
    override fun detectBeneficiary(): BeneficiaryDto {
        TODO("Not yet implemented")
    }

    override fun detectRisks(): CompanyRisksDto {
        TODO("Not yet implemented")
    }

    override fun detectOpportunities(): CompanyOpportunitiesDto {
        TODO("Not yet implemented")
    }

    override fun provideDescription(documentId: Long): String {
        val doc = documentService.get(documentId)
        val fileContent = s3Service.download(doc.path!!)
        val userId = doc.userId!!
        val fullDocName = doc.getNameWithExtension();

        userOpenAiUserService.uploadFile(userId, fileContent.inputStream, fullDocName)
        val result = userOpenAiUserService.startDialogWIthUserPrompt(userId, documentDescriptionPrompt())
        userOpenAiUserService.deleteFilesFromOpenAi(userId)

        doc.description = result
        documentService.save(doc)
        return result!!
    }
}