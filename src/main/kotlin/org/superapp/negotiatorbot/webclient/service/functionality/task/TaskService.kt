package org.superapp.negotiatorbot.webclient.service.functionality.task

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.Project
import org.superapp.negotiatorbot.webclient.entity.task.Task
import org.superapp.negotiatorbot.webclient.entity.task.TaskStatus
import org.superapp.negotiatorbot.webclient.entity.task.TaskType
import org.superapp.negotiatorbot.webclient.enum.BusinessType
import org.superapp.negotiatorbot.webclient.repository.TaskRepository

interface TaskService {
    fun getTask(document: DocumentMetadata): Task
    fun getTask(project: Project): Task
    fun changeStatus(task: Task, status: TaskStatus): Task
    fun deleteTask(taskId: Long)
}

@Service
class TaskServiceImpl(
    private val taskRepository: TaskRepository,
) : TaskService {

    override fun getTask(document: DocumentMetadata): Task {
        val type = document.statusMapping()
        val relatedId = document.id!!
        return findOrCreate(type, relatedId)
    }

    override fun getTask(project: Project): Task {
        val relatedId = project.id!!
        return findOrCreate(TaskType.PROJECT, relatedId)
    }

    override fun changeStatus(task: Task, status: TaskStatus): Task {
        task.status = status
        return taskRepository.save(task)
    }

    override fun deleteTask(taskId: Long) {
        taskRepository.deleteById(taskId)
    }

    private fun findOrCreate(type: TaskType, relatedId: Long): Task {
        val task = taskRepository.findByTaskTypeAndRelatedId(type, relatedId)
            ?.let { changeStatus(it, TaskStatus.CREATED) }
        return task ?: createTask(type, relatedId)
    }

    private fun createTask(type: TaskType, relatedId: Long): Task {
        val task = Task(
            taskType = type,
            relatedId = relatedId,
        )
        return taskRepository.save(task)
    }

    private fun DocumentMetadata.statusMapping(): TaskType {
        return when (businessType) {
            BusinessType.USER -> TaskType.COMPANY
            BusinessType.PARTNER -> TaskType.CONTRACTOR
            BusinessType.PROJECT -> TaskType.PROJECT
            else -> throw IllegalArgumentException("Unknown business type: ${businessType}")
        }
    }
}