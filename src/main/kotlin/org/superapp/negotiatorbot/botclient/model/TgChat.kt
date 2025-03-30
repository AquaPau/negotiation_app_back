package org.superapp.negotiatorbot.botclient.model

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.entity.TaskEnabled
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.PromptType
import org.telegram.telegrambots.meta.api.objects.Document
import java.net.URL

@Entity
@Table(name = "tg_chats")
class TgChat (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    val chatId: Long,

    @Column(nullable = false, unique = true)
    val tgUserId: Long,

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    var chosenDocumentType: DocumentType? = null,

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    var chosenPromptType: PromptType? = null,

    //its one time so better not to store it at all
    @Transient
    var document: Document? = null,
) : TaskEnabled {
}