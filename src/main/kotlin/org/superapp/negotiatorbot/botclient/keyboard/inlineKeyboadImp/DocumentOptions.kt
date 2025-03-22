package org.superapp.negotiatorbot.botclient.keyboard.inlineKeyboadImp

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.DocumentTypeHandlerTG
import org.superapp.negotiatorbot.botclient.keyboard.KeyBoardWithHandler
import org.superapp.negotiatorbot.webclient.enum.DocumentType

const val DOC_TYPE_CALLBACK = "documentTypeCallback,chose:"

abstract class DocumentOption(
    private val userView: String,
    private val docType: DocumentType,
    handler: DocumentTypeHandlerTG
) : KeyBoardWithHandler(handler) {

    override fun userView(): String = userView

    override fun callBackData(): String = DOC_TYPE_CALLBACK + docType.name
}

@Component
class LaborContractOption(handler: DocumentTypeHandlerTG) : DocumentOption(
    "Трудовой договор",
    DocumentType.LABOR_CONTRACT, handler
)

@Component
class RealEstateContractOption(handler: DocumentTypeHandlerTG) : DocumentOption(
    "Договор аренды",
    DocumentType.REAL_ESTATE_LEASE_CONTRACT, handler
)