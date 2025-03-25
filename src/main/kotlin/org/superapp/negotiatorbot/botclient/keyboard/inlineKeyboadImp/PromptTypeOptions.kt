package org.superapp.negotiatorbot.botclient.keyboard.inlineKeyboadImp

import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.botclient.handler.callbackhandler.PromptTypeCallbackQueryHandler
import org.superapp.negotiatorbot.botclient.keyboard.KeyBoardWithHandler
import org.superapp.negotiatorbot.webclient.enums.PromptType

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
    "Описание содержания",
    PromptType.DESCRIPTION,
    handler
)

@Component
class RisksPromptTypeOption(handler: PromptTypeCallbackQueryHandler) : PromptTypeOption(
    "Анализ рисков",
    PromptType.RISKS,
    handler
)


