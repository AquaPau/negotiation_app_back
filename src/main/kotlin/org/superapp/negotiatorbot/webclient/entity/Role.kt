package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*

@Entity
@Table(name = "roles")
class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(nullable = false, unique = true)
    var name: String? = null

    @ManyToMany(mappedBy = "roles")
    val users: List<User> = ArrayList<User>()
}
