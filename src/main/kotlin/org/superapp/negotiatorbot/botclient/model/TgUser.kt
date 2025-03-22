package org.superapp.negotiatorbot.botclient.model

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import org.superapp.negotiatorbot.webclient.enum.PromptType

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
    @Enumerated(EnumType.STRING)
    var chosenDocumentType: DocumentType? = null,

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    var chosenPromptType: PromptType? = null,

    @Column(nullable = true)
    var assistantId: Long? = null,
) {

}