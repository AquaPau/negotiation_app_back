package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.dto.project.ProjectDto
import org.superapp.negotiatorbot.webclient.dto.project.ProjectSlimDto
import org.superapp.negotiatorbot.webclient.dto.project.ProjectTaskHistory
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord

@Entity
@Table(name = "projects")
class Project : TaskEnabled {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(targetEntity = User::class, fetch = FetchType.EAGER)
    var user: User? = null

    @Column
    var customUserGeneratedName: String = ""

    @Column
    var userGeneratedPrompt: String = ""

    @Column(nullable = true, unique = true)
    var assistantDbId: Long? = null
}

fun Project.toDto(tasks: List<TaskRecord>?) =
    ProjectDto(
        id = this.id!!,
        customUserGeneratedName = this.customUserGeneratedName,
        userId = user!!.id!!,
        userGeneratedPrompt = userGeneratedPrompt,
        taskResult = tasks?.first()?.result,
        taskHistory = tasks?.map {
            ProjectTaskHistory(
                id = it.id!!,
                status = it.status
            )
        } ?: listOf()
    )

fun Project.toDto() =
    ProjectSlimDto(
        id = this.id!!,
        customUserGeneratedName = this.customUserGeneratedName,
        userId = user!!.id!!
    )
