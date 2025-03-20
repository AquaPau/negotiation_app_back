package org.superapp.negotiatorbot.webclient.service.task

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.entity.TaskEnabled
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.exception.TaskException
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService
import org.superapp.negotiatorbot.webclient.service.functionality.openai.OpenAiUserService
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskRecordService

@Service
class OpenAiTaskService(
    taskService: TaskRecordService,
    private val openAiUserService: OpenAiUserService,
    private val documentService: DocumentService
) :
    AsyncTaskService<DocumentMetadata>(taskService) {
    override fun run(task: TaskRecord, taskEnabled: TaskEnabled, vararg data: Any) {
        if (taskEnabled is DocumentMetadata) {
            val prompt = if (data.isNotEmpty()) data.get(0).toString()
            else throw TaskException(TaskStatus.ERROR_PARSE_PARAMS)
            val promptType = if (data.size > 1) data.get(1) as PromptType
            else throw TaskException(TaskStatus.ERROR_PARSE_PARAMS)
            if (promptType == PromptType.DESCRIPTION) {
                taskEnabled.description = openAiUserService.provideResponseFromOpenAi(task, taskEnabled, prompt)
                documentService.save(taskEnabled)
            } else if (promptType == PromptType.RISKS) {
                taskEnabled.risks = openAiUserService.provideResponseFromOpenAi(task, taskEnabled, prompt)
                documentService.save(taskEnabled)
            }
        } else if (taskEnabled is Project) {
            val prompt = if (data.isNotEmpty()) data.get(0).toString()
            else throw TaskException(TaskStatus.ERROR_PARSE_PARAMS)
            //TODO finish
        }
    }
}