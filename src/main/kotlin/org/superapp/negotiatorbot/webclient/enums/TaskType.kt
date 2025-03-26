package org.superapp.negotiatorbot.webclient.enums

enum class TaskType {
    COMPANY_DOCUMENT_DESCRIPTION, COMPANY_DOCUMENT_RISKS, CONTRACTOR_DOCUMENT_DESCRIPTION, CONTRACTOR_DOCUMENT_RISKS, PROJECT_RESOLUTION;

    fun toPromptType(): PromptType {
        return when (this) {
            COMPANY_DOCUMENT_DESCRIPTION, CONTRACTOR_DOCUMENT_DESCRIPTION -> PromptType.DESCRIPTION
            COMPANY_DOCUMENT_RISKS, CONTRACTOR_DOCUMENT_RISKS -> PromptType.RISKS
            PROJECT_RESOLUTION -> PromptType.PROJECT
        }
    }
}