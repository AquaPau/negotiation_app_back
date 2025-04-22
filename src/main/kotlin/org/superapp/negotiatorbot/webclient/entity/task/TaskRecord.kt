package org.superapp.negotiatorbot.webclient.entity.task

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.dto.TaskRecordDto
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.enums.TaskType
import org.superapp.negotiatorbot.webclient.enums.toDto

@Entity
@Table(name = "tasks")
class TaskRecord(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TaskStatus = TaskStatus.CREATED,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "task_type")
    val taskType: TaskType,

    @Column(nullable = false, name = "related_id")
    val relatedId: Long,

    @Column(nullable = true, name = "result")
    var result: String? = null
)

fun TaskRecord.toDto() =
    TaskRecordDto(
        id = this.id!!,
        relatedId = this.relatedId,
        result = this.result,
        status = this.status.toDto()
    )