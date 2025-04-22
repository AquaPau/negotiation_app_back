package org.superapp.negotiatorbot.webclient.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.enums.TaskType

@Repository
interface TaskRecordRepository : JpaRepository<TaskRecord, Long?> {
    fun findAllByTaskTypeAndRelatedIdOrderByIdDesc(taskType: TaskType, relatedId: Long): List<TaskRecord>
    fun findAllByRelatedIdInAndTaskTypeIn(relatedId: List<Long>, taskType: List<TaskType>): List<TaskRecord>
}