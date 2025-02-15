package org.superapp.negotiatorbot.webclient.entity.assistant

import com.aallam.openai.api.vectorstore.VectorStoreId
import jakarta.persistence.*

@Entity
@Table(name = "assistant_file_storages")
class OpenAiAssistantFileStorage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(unique = true, nullable = false)
    var openAiVectorStoreId : String? = null

    @OneToMany(
        mappedBy = "openAiAssistantFileStorage",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
   var openAiFiles: MutableList<OpenAiFile> = ArrayList()

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    var openAiAssistant: OpenAiAssistant? = null

    fun getVectorStoreId() = VectorStoreId(openAiVectorStoreId!!)

}