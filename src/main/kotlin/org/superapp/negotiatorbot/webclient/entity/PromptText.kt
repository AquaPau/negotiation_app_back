package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.enum.LegalType
import org.superapp.negotiatorbot.webclient.enum.PromptType

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
    var type: LegalType? = null

    @Column
    var promptText: String? = null

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    var promptType: PromptType? = null
}