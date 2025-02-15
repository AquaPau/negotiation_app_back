package org.superapp.negotiatorbot.webclient.entity.assistant

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.entity.User

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

    @OneToOne(mappedBy ="openAiAssistant",cascade = [CascadeType.ALL], optional = true, fetch = FetchType.EAGER, orphanRemoval = true)
    var openAiAssistantFileStorage: OpenAiAssistantFileStorage? = null

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null

    @OptIn(BetaOpenAI::class)
    fun getAssistantId(): AssistantId {
        return AssistantId(assistantId!!)
    }
}