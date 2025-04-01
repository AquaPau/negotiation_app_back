package org.superapp.negotiatorbot.botclient.keyboard.inlineKeyboadImp

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.DocumentTypeQueryHandler
import org.superapp.negotiatorbot.webclient.enums.DocumentType


class DocumentOptionFactory/*(
    private val userView: String,
    private val docType: DocumentType,
    private val objectMapper: ObjectMapper,
    handler: DocumentTypeQueryHandler
)  {

    override fun userView(): String = userView

    override fun callBackData(): String{
        val
    }
}

@Component
class LaborContractOption(handler: DocumentTypeQueryHandler) : DocumentOption(
    "Трудовой договор",
    DocumentType.LABOR_CONTRACT, handler
)

@Component
class RealEstateContractOption(handler: DocumentTypeQueryHandler) : DocumentOption(
    "Договор аренды",
    DocumentType.REAL_ESTATE_LEASE_CONTRACT, handler
)

data class DocumentTypeWrapper(
    val documentType: DocumentType,
    val dbFileId: Long
)*/