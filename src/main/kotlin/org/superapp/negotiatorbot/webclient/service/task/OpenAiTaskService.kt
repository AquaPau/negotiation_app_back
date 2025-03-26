package org.superapp.negotiatorbot.webclient.service.task

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.entity.TaskEnabled
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.exception.TaskException
import org.superapp.negotiatorbot.webclient.service.documentMetadata.DocumentService
import org.superapp.negotiatorbot.webclient.service.functionality.openai.OpenAiUserService
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskRecordService

@Service
class OpenAiTaskService(
    private val taskService: TaskRecordService,
    private val openAiUserService: OpenAiUserService,
) :
    AsyncTaskService<DocumentMetadata>(taskService) {
    override fun run(task: TaskRecord, taskEnabled: TaskEnabled, vararg data: Any) {
        if (taskEnabled is DocumentMetadata) {
            val prompt = if (data.isNotEmpty()) data[0].toString()
            else throw TaskException(TaskStatus.ERROR_PARSE_PARAMS)
            val result = openAiUserService.provideResponseFromOpenAi(task, taskEnabled, prompt)
            taskService.updateResult(task.id!!, result)

        } else if (taskEnabled is Project) {
            val prompt = if (data.isNotEmpty()) data[0].toString()
            else throw TaskException(TaskStatus.ERROR_PARSE_PARAMS)
            //TODO
        }
    }
}