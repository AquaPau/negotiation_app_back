package org.superapp.negotiatorbot.webclient.service.functionality.task

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.entity.TaskEnabled
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.enums.TaskType
import org.superapp.negotiatorbot.webclient.exception.TaskException
import org.superapp.negotiatorbot.webclient.repository.TaskRecordRepository

interface TaskRecordService {
    fun getById(id: Long): TaskRecord
    fun getByTypeAndReference(type: TaskType, referenceId: Long): TaskRecord

    fun createTask(taskEnabled: TaskEnabled, vararg data: Any): TaskRecord
    fun changeStatus(task: TaskRecord, status: TaskStatus): TaskRecord
    fun deleteTask(taskId: Long)

    fun updateResult(taskId: Long, result: String): TaskRecord
}

@Service
@Transactional
class TaskRecordServiceImpl(
    private val taskRepository: TaskRecordRepository,
) : TaskRecordService {
    override fun getById(id: Long): TaskRecord {
        return taskRepository.findById(id).orElseThrow()
    }

    override fun getByTypeAndReference(type: TaskType, referenceId: Long): TaskRecord {
        return taskRepository.findByTaskTypeAndRelatedId(type, referenceId)
            ?: throw NoSuchElementException("No such task with type: [$type] related id: [$referenceId]")
    }

    override fun createTask(taskEnabled: TaskEnabled, vararg data: Any): TaskRecord {
        val type = when (taskEnabled) {
            is DocumentMetadata -> {
                val promptType = if (data.isNotEmpty()) data[1] as PromptType
                else throw TaskException(TaskStatus.ERROR_PARSE_PARAMS)
                taskEnabled.defineTaskType(promptType)
            }

            is Project -> {
                TaskType.PROJECT_RESOLUTION
            }

            else -> throw UnsupportedOperationException()
        }
        val relatedId = when (taskEnabled) {
            is DocumentMetadata -> taskEnabled.id!!
            is Project -> taskEnabled.id!!
            else -> throw UnsupportedOperationException()
        }
        val task = TaskRecord(
            taskType = type,
            relatedId = relatedId,
        )
        return taskRepository.save(task)
    }

    override fun changeStatus(task: TaskRecord, status: TaskStatus): TaskRecord {
        task.status = status
        return taskRepository.save(task)
    }

    override fun deleteTask(taskId: Long) {
        taskRepository.deleteById(taskId)
    }

    override fun updateResult(taskId: Long, result: String): TaskRecord {
        val task = taskRepository.findById(taskId).orElseThrow { NoSuchElementException() }
        task.result = result
        return taskRepository.save(task)
    }

    private fun DocumentMetadata.defineTaskType(promptType: PromptType): TaskType {
        return when (businessType) {
            BusinessType.USER -> if (promptType == PromptType.RISKS)
                TaskType.COMPANY_DOCUMENT_RISKS
            else TaskType.COMPANY_DOCUMENT_DESCRIPTION

            BusinessType.PARTNER -> if (promptType == PromptType.RISKS)
                TaskType.CONTRACTOR_DOCUMENT_RISKS
            else TaskType.CONTRACTOR_DOCUMENT_DESCRIPTION

            else -> throw IllegalArgumentException("Unknown business type: $businessType")
        }
    }
}