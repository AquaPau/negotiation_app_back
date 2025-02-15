package org.superapp.negotiatorbot.webclient.entity.assistant

import com.aallam.openai.api.file.FileId
import jakarta.persistence.*

@Entity
@Table(name = "open_ai_files")
class OpenAiFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, unique = true)
    var fileId: String? = null

    @ManyToOne
    @JoinColumn(name = "assistant_file_storages_id", nullable = false)
    var openAiAssistantFileStorage: OpenAiAssistantFileStorage? = null

    fun getFileId() = FileId(fileId!!)
}