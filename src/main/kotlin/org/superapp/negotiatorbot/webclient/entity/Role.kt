package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.enum.Roles

@Entity
@Table(name = "roles")
class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(nullable = false, unique = true)
    @Enumerated(value = EnumType.STRING)
    var name: Roles? = null

    @ManyToMany(mappedBy = "roles")
    val users: List<User> = listOf()
}
