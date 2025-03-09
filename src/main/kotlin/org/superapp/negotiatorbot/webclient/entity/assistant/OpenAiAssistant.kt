package org.superapp.negotiatorbot.webclient.entity.assistant

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import jakarta.persistence.*

@Entity
@Table(name = "open_ai_assistants")
class OpenAiAssistant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, unique = true)
    var assistantId: String? = null

    @Column(nullable = false, unique = true)
    var threadId: String? = null

    @OneToOne(mappedBy = "openAiAssistant", cascade = [CascadeType.ALL], optional = true, orphanRemoval = true)
    var openAiAssistantFileStorage: OpenAiAssistantFileStorage? = null

    @OptIn(BetaOpenAI::class)
    fun getAssistantId(): AssistantId {
        return AssistantId(assistantId!!)
    }
}