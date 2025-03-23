package org.superapp.negotiatorbot.botclient.keyboard.inlineKeyboadImp

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.PromptTypeCallbackQueryHandler
import org.superapp.negotiatorbot.botclient.keyboard.KeyBoardWithHandler
import org.superapp.negotiatorbot.webclient.enum.PromptType

const val PROMPT_TYPE_CALLBACK = "analysisTypeCallback,chose:"

abstract class PromptTypeOption(
    private val userView: String,
    private val analysisType: PromptType,
    handler: PromptTypeCallbackQueryHandler
) : KeyBoardWithHandler(handler) {

    override fun userView(): String = userView
    override fun callBackData(): String = PROMPT_TYPE_CALLBACK + analysisType
}

@Component
class DescriptionPromptTypeOption(handler: PromptTypeCallbackQueryHandler) : PromptTypeOption(
    "Общий анализ документа",
    PromptType.DESCRIPTION,
    handler
)

@Component
class RisksPromptTypeOption(handler: PromptTypeCallbackQueryHandler) : PromptTypeOption(
    "Проанализировать риски документа",
    PromptType.RISKS,
    handler
)


