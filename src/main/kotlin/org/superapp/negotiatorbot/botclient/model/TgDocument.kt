package org.superapp.negotiatorbot.botclient.model

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.entity.TaskEnabled
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.PromptType

@Entity
@Table(name = "tg_documents")
class TgDocument(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    val tgUserDbId: Long,

    @Column(nullable = false)
    val chatId: Long,

    @Column(nullable = true)
    var messageIdOfDocumentUploadingMessage: Int? = null,

    @Column(nullable = true)
    var messageId: Int? = null,

    @Column(nullable = true)
    var tgFileId: String? = null,

    @Column(nullable = true)
    var tgDocumentName: String? = null,

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    var chosenDocumentType: DocumentType? = null,

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    var chosenPromptType: PromptType? = null,

    ) : TaskEnabled {
}