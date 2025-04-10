package org.superapp.negotiatorbot.webclient.service.task

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.botclient.service.openAi.TgDocumentOpenAiService
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.entity.TaskEnabled
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.exception.TaskException
import org.superapp.negotiatorbot.webclient.service.functionality.openai.WebOpenAiService
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskRecordService

@Service
class OpenAiTaskService(
    taskService: TaskRecordService,
    private val webOpenAiService: WebOpenAiService,
    private val tgDocumentOpenAiService: TgDocumentOpenAiService,
) : AsyncTaskService<DocumentMetadata>(taskService) {
    @Transactional
    override fun run(task: TaskRecord, taskEnabled: TaskEnabled, vararg data: Any): TaskRecord {
        val args = (if (data is Array) data[0] else emptyArray<Any>())
        if (taskEnabled is DocumentMetadata) {
            val prompt = getSinglePrompt(args)
            val result = webOpenAiService.provideResponseFromOpenAi(task, taskEnabled, prompt)
            return taskService.updateResult(task.id!!, result)
        } else if (taskEnabled is Project) {
            val prompt = getSinglePrompt(args)
            if (args is Array<*> && args.size > 2 && args[1] as PromptType != PromptType.PROJECT) throw TaskException(
                TaskStatus.ERROR_PARSE_PARAMS
            )
            val documents = if (args is Array<*> && args.size > 2) args[2] as List<DocumentMetadata>
            else throw TaskException(TaskStatus.ERROR_PARSE_PARAMS)
            val result = webOpenAiService.provideResponseFromOpenAi(task, documents, prompt)
            return taskService.updateResult(task.id!!, result)
        } else if (taskEnabled is TgDocument) {
            val prompt = getSinglePrompt(args)
            val result = tgDocumentOpenAiService.provideResponseFromOpenAi(task, taskEnabled, prompt)
            return taskService.updateResult(task.id!!, result)
        } else throw UnsupportedOperationException()
    }

    private fun getSinglePrompt(args: Any) = if (args is Array<*> && args.isNotEmpty()) args[0].toString()
    else throw TaskException(TaskStatus.ERROR_PARSE_PARAMS)


}