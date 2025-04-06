package org.superapp.negotiatorbot.webclient.service.functionality.task

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.superapp.negotiatorbot.botclient.model.TgDocument
import org.superapp.negotiatorbot.webclient.dto.TaskRecordDto
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.entity.TaskEnabled
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.entity.task.toDto
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.enums.TaskType
import org.superapp.negotiatorbot.webclient.exception.TaskException
import org.superapp.negotiatorbot.webclient.exception.TaskNotFoundException
import org.superapp.negotiatorbot.webclient.repository.TaskRecordRepository

interface TaskRecordService {
    fun getById(id: Long): TaskRecordDto
    fun getLastByTypeAndReference(type: TaskType, referenceId: Long): TaskRecord

    fun getAllByTypeAndReference(type: TaskType, referenceId: Long): List<TaskRecord>

    fun createTask(taskEnabled: TaskEnabled, vararg data: Any): TaskRecord
    fun changeStatus(task: TaskRecord, status: TaskStatus): TaskRecord
    fun deleteTask(taskId: Long)

    fun updateResult(taskId: Long, result: String): TaskRecord
}

@Service
class TaskRecordServiceImpl(
    private val taskRepository: TaskRecordRepository,
) : TaskRecordService {
    override fun getById(id: Long): TaskRecordDto {
        return taskRepository.findById(id).orElseThrow { TaskNotFoundException(id)}.toDto()
    }

    override fun getLastByTypeAndReference(type: TaskType, referenceId: Long): TaskRecord {
        return taskRepository.findFirstByTaskTypeAndRelatedIdOrderByIdDesc(type, referenceId)
            ?: throw TaskNotFoundException(null, referenceId)
    }

    override fun getAllByTypeAndReference(type: TaskType, referenceId: Long): List<TaskRecord> {
        return taskRepository.findAllByTaskTypeAndRelatedIdOrderByIdDesc(type, referenceId)
            .ifEmpty { throw TaskNotFoundException(null, referenceId) }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun createTask(taskEnabled: TaskEnabled, vararg data: Any): TaskRecord {
        val args = (if (data is Array) data[0] else emptyArray<Any>())
        val type = when (taskEnabled) {
            is DocumentMetadata -> {
                val promptType = if (args is Array<*> && args.size > 1) args[1] as PromptType
                else throw TaskException(TaskStatus.ERROR_PARSE_PARAMS)
                taskEnabled.defineTaskType(promptType)
            }

            is Project -> {
                TaskType.PROJECT_RESOLUTION
            }

            is TgDocument -> {
                TaskType.TG_DOCUMENT
            }

            else -> throw UnsupportedOperationException()
        }
        val relatedId = when (taskEnabled) {
            is DocumentMetadata -> taskEnabled.id!!
            is Project -> taskEnabled.id!!
            is TgDocument -> taskEnabled.id!!
            else -> throw UnsupportedOperationException()
        }
        val task = TaskRecord(
            taskType = type,
            relatedId = relatedId,
        )
        return taskRepository.save(task)
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun changeStatus(task: TaskRecord, status: TaskStatus): TaskRecord {
        task.status = status
        return taskRepository.saveAndFlush(task)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun deleteTask(taskId: Long) {
        taskRepository.deleteById(taskId)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun updateResult(taskId: Long, result: String): TaskRecord {
        val task = taskRepository.findById(taskId).orElseThrow { TaskNotFoundException(taskId) }
        task.result = result
        return taskRepository.saveAndFlush(task)
    }

    private fun DocumentMetadata.defineTaskType(promptType: PromptType): TaskType {
        return when (businessType) {
            BusinessType.USER -> if (promptType == PromptType.RISKS)
                TaskType.COMPANY_DOCUMENT_RISKS
            else TaskType.COMPANY_DOCUMENT_DESCRIPTION

            BusinessType.PARTNER -> if (promptType == PromptType.RISKS)
                TaskType.CONTRACTOR_DOCUMENT_RISKS
            else TaskType.CONTRACTOR_DOCUMENT_DESCRIPTION

            else -> throw UnsupportedOperationException("Unknown business type: $businessType")
        }
    }
}