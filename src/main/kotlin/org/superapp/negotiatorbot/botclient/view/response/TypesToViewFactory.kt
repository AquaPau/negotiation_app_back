package org.superapp.negotiatorbot.botclient.view.response

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.PromptType

@Component
class TypesToViewFactory {

    val documentTypes = mapOf(
        DocumentType.LABOR_CONTRACT_EMPLOYEE to "Трудовой договор",
        DocumentType.REAL_ESTATE_LEASE_CONTRACT_TENANT to "Договор аренды",
        DocumentType.SALES_CONTRACT_CUSTOMER to "Договор купли-продажи",
        DocumentType.SERVICE_CONTRACT_CUSTOMER to "Договор работ/услуг",
        DocumentType.DEFAULT to "Другой тип"
    )

    val promptTypes = mapOf(
        PromptType.DESCRIPTION to "Описание содержания документа",
        PromptType.RISKS to "Риски, связанные с документом"
    )

    fun viewOf(docType: DocumentType) =
        documentTypes[docType] ?: throw IllegalArgumentException("Cannot convert $this to View")

    fun viewOf(promptType: PromptType) =
        promptTypes[promptType] ?: throw IllegalArgumentException("Cannot convert $this to View")

}