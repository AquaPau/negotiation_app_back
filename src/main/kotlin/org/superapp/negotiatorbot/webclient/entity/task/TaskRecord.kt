package org.superapp.negotiatorbot.webclient.entity.task

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.enums.TaskType

@Entity
@Table(name = "tasks", uniqueConstraints = [UniqueConstraint(columnNames = ["task_type", "related_id"])])
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