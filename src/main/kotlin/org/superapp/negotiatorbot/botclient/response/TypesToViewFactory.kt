package org.superapp.negotiatorbot.botclient.response

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.PromptType

@Component
class TypesToViewFactory {

    val documentTypes = mapOf(
        DocumentType.LABOR_CONTRACT to "Трудовой договор",
        DocumentType.REAL_ESTATE_LEASE_CONTRACT to "Договор аренды",
        DocumentType.SALES_CONTRACT to "Договор купли-продажи",
        DocumentType.SERVICE_CONTRACT to "Договор обслуживания",
        DocumentType.OTHER to "Другой тип"
    )

    val promptTypes = mapOf(
        PromptType.DESCRIPTION to "Описание содержания документа",
        PromptType.RISKS to "Риски связанные с документом"
    )

    fun viewOf(docType: DocumentType) =
        documentTypes[docType] ?: throw IllegalArgumentException("Cannot convert $this to View")

    fun viewOf(promptType: PromptType) =
        promptTypes[promptType] ?: throw IllegalArgumentException("Cannot convert $this to View")

}