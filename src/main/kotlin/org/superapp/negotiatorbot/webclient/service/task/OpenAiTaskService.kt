package org.superapp.negotiatorbot.webclient.service.task

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.superapp.negotiatorbot.botclient.model.TgChat
import org.superapp.negotiatorbot.botclient.service.openAi.OpenAiTgChatService
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.entity.TaskEnabled
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.exception.TaskException
import org.superapp.negotiatorbot.webclient.service.functionality.openai.OpenAiUserService
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskRecordService

@Service
class OpenAiTaskService(
    taskService: TaskRecordService,
    private val openAiUserService: OpenAiUserService,
    private val openAiTgChatService: OpenAiTgChatService,
) : AsyncTaskService<DocumentMetadata>(taskService) {
    @Transactional
    override fun run(task: TaskRecord, taskEnabled: TaskEnabled, vararg data: Any): TaskRecord {
        val args = (if (data is Array) data[0] else emptyArray<Any>())
        if (taskEnabled is DocumentMetadata ) {
            val prompt = getSinglePrompt(args)
            val result = openAiUserService.provideResponseFromOpenAi(task, taskEnabled, prompt)
            return taskService.updateResult(task.id!!, result)
        } else if (taskEnabled is Project) {
            val prompt = getSinglePrompt(args)
            if (args is Array<*> && args.size > 2 && args[1] as PromptType != PromptType.PROJECT) throw TaskException(
                TaskStatus.ERROR_PARSE_PARAMS
            )
            val documents = if (args is Array<*> && args.size > 2) args[2] as List<DocumentMetadata>
            else throw TaskException(TaskStatus.ERROR_PARSE_PARAMS)
            val result = openAiUserService.provideResponseFromOpenAi(task, documents, prompt)
            return taskService.updateResult(task.id!!, result)
        } else if(taskEnabled is TgChat) {
            val prompt = getSinglePrompt(args)
            val result = openAiTgChatService.provideResponseFromOpenAi(task, taskEnabled, prompt)
            return taskService.updateResult(task.id!!, result)
        } else throw UnsupportedOperationException()
    }

    private fun getSinglePrompt(args: Any) = if (args is Array<*> && args.isNotEmpty()) args[0].toString()
    else throw TaskException(TaskStatus.ERROR_PARSE_PARAMS)


}