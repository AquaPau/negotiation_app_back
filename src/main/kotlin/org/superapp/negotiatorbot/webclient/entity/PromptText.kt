package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.LegalType
import org.superapp.negotiatorbot.webclient.enums.PromptType

@Table(name = "prompt_data")
@Entity
class PromptText {

    @Id
    var id: Long? = null

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    var auditory: LegalType? = null

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    var type: DocumentType? = null

    @Column
    var promptText: String? = null

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    var promptType: PromptType? = null
}