package org.superapp.negotiatorbot.botclient.service.openAi

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.model.TelegramDocumentCounterpartyType
import org.superapp.negotiatorbot.botclient.model.TelegramDocumentType
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.LegalType
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.superapp.negotiatorbot.webclient.exception.PromptNotFoundException
import org.superapp.negotiatorbot.webclient.service.functionality.PromptTextService
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskRecordService
import org.superapp.negotiatorbot.webclient.service.task.OpenAiTaskService

@Service
class AnalyseTgService(
    private val promptTextService: PromptTextService,
    private val openAiTaskService: OpenAiTaskService,
    private val taskRecordService: TaskRecordService
) {

    suspend fun analyseDoc(tgDocument: TgDocument): String {
        val taskId = openAiTaskService.execute(tgDocument, createPrompt(tgDocument)).await().id!!
        return taskRecordService.getById(taskId).result
            ?: "Возникла ошибка при анализе. Пожалуйста, попробуйте снова"
    }

    private fun createPrompt(tgDocument: TgDocument): String {
        val introPrompt = promptTextService.fetchPrompt(LegalType.INDIVIDUAL, DocumentType.DEFAULT, PromptType.DEFAULT)
        val prompt = try {
            promptTextService.fetchPrompt(
                LegalType.INDIVIDUAL,
                defineDocumentType(tgDocument),
                PromptType.CHECKLIST
            )
        } catch (e: PromptNotFoundException) {
            promptTextService.fetchPrompt(LegalType.INDIVIDUAL, DocumentType.DEFAULT, PromptType.CHECKLIST)
        }
        return "$introPrompt $prompt"
    }

    companion object {

        private fun defineDocumentType(tgDocument: TgDocument): DocumentType {
            return when (tgDocument.chosenDocumentType) {
                TelegramDocumentType.SERVICE_CONTRACT -> when (tgDocument.chosenCounterpartyType) {
                    TelegramDocumentCounterpartyType.CONTRACTOR -> DocumentType.SERVICE_CONTRACT_CONTRACTOR
                    TelegramDocumentCounterpartyType.SERVICES_CUSTOMER -> DocumentType.SERVICE_CONTRACT_CUSTOMER
                    else -> throw UnsupportedOperationException()
                }

                TelegramDocumentType.SALES_CONTRACT -> when (tgDocument.chosenCounterpartyType) {
                    TelegramDocumentCounterpartyType.SELLER -> DocumentType.SALES_CONTRACT_SELLER
                    TelegramDocumentCounterpartyType.GOODS_CUSTOMER -> DocumentType.SALES_CONTRACT_CUSTOMER
                    else -> throw UnsupportedOperationException()
                }

                else -> throw UnsupportedOperationException()
            }
        }
    }
}