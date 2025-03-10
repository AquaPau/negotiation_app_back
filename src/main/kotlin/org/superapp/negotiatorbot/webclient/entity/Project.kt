package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.dto.project.ProjectDto

@Entity
@Table(name = "projects")
class Project {
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

fun Project.toDto() =
    ProjectDto(
        customUserGeneratedName = this.customUserGeneratedName,
        userId = user!!.id!!,
        userGeneratedPrompt = userGeneratedPrompt
    )
