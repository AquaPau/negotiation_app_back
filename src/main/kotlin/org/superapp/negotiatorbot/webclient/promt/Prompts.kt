package org.superapp.negotiatorbot.webclient.promt

const val COMPANY_INFO =
    "from provided docs, return only INN strictly as a number." +
            " Use only documents CURRENTLY available in your vector store. DONT use any knowledge from previous thread.  " +
            "dont return nothng else. NOT a single word. Just INN= ????" +
            "Only INN (ИНН) number. format: " +
            "INN=???"


fun documentDescriptionPrompt(): String {
    return """
        На основании предоставленного документа  соствь его описание, 
        Общая длина ответ не должна превышать 5000 символов
        
        Структура ответа:
        1. Одно предложение с его общей характеристикой.
        2. Описание содержания этого документа на русском языке.
    """.trimIndent()
}

fun documentRisksPrompt(): String {
    return """
        На основании предоставленного документа  соствь описание рисков документа, 
         Общая длина ответ не должна превышать 5000 символов.
        
        Структура ответа:
        1. Одно предложение с его перечистелеим рисков.
        2. Буллет поинты по каждому рисвку с подробным описанием.
    """.trimIndent()

}

