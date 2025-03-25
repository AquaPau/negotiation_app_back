package org.superapp.negotiatorbot.botclient.keyboard.inlineKeyboadImp

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.DocumentTypeHandlerQuery
import org.superapp.negotiatorbot.botclient.keyboard.KeyBoardWithHandler
import org.superapp.negotiatorbot.webclient.enums.DocumentType

const val DOC_TYPE_CALLBACK = "documentTypeCallback,chose:"

abstract class DocumentOption(
    private val userView: String,
    private val docType: DocumentType,
    handler: DocumentTypeHandlerQuery
) : KeyBoardWithHandler(handler) {

    override fun userView(): String = userView

    override fun callBackData(): String = DOC_TYPE_CALLBACK + docType.name
}

@Component
class LaborContractOption(handler: DocumentTypeHandlerQuery) : DocumentOption(
    "Трудовой договор",
    DocumentType.LABOR_CONTRACT, handler
)

@Component
class RealEstateContractOption(handler: DocumentTypeHandlerQuery) : DocumentOption(
    "Договор аренды",
    DocumentType.REAL_ESTATE_LEASE_CONTRACT, handler
)