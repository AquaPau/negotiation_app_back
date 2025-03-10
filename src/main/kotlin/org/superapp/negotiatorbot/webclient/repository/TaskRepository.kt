package org.superapp.negotiatorbot.webclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.superapp.negotiatorbot.webclient.entity.task.Task
import org.superapp.negotiatorbot.webclient.entity.task.TaskType

@Repository
interface TaskRepository : JpaRepository<Task, Long?> {
    fun findByTaskTypeAndRelatedId(taskType: TaskType, relatedId: Long): Task?
}