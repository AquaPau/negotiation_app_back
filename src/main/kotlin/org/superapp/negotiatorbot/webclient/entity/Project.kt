package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*

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
}