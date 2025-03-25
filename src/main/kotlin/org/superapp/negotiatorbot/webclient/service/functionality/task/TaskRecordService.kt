package org.superapp.negotiatorbot.webclient.service.functionality.task

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.entity.TaskEnabled
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.enums.TaskType
import org.superapp.negotiatorbot.webclient.repository.TaskRecordRepository

interface TaskRecordService {
    fun getById(id: Long): TaskRecord
    fun getByTypeAndReference(type: TaskType, referenceId: Long): TaskRecord

    fun createTask(taskEnabled: TaskEnabled): TaskRecord
    fun changeStatus(task: TaskRecord, status: TaskStatus): TaskRecord
    fun deleteTask(taskId: Long)
}

@Service
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

    override fun createTask(taskEnabled: TaskEnabled): TaskRecord {
        val type = when (taskEnabled) {
            is DocumentMetadata -> {
                taskEnabled.defineTaskType()
            }

            is Project -> {
                TaskType.PROJECT
            }

            else -> throw UnsupportedOperationException()
        }
        val relatedId = when (taskEnabled) {
            is DocumentMetadata -> {
                taskEnabled.relatedId!!
            }

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

    private fun DocumentMetadata.defineTaskType(): TaskType {
        return when (businessType) {
            BusinessType.USER -> TaskType.COMPANY
            BusinessType.PARTNER -> TaskType.CONTRACTOR
            BusinessType.PROJECT -> TaskType.PROJECT
            else -> throw IllegalArgumentException("Unknown business type: $businessType")
        }
    }
}