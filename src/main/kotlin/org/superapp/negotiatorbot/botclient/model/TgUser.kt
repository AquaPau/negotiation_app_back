package org.superapp.negotiatorbot.botclient.model

import jakarta.persistence.*

@Entity
@Table(name = "tg_users")
class TgUser(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    val tgId: Long,

    @Column(nullable = false)
    val tgUsername: String,

    @Column(nullable = true)
    var assistantDbId: Long? = null,
) {

}