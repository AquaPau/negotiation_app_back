package org.superapp.negotiatorbot.botclient.handler.documentHandler

import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.botclient.config.BotConfig
import org.superapp.negotiatorbot.botclient.service.SenderService
import org.telegram.telegrambots.meta.api.methods.GetFile
import  org. telegram. telegrambots. meta. api. objects. Document
import java.io.File
import java.net.URI
import java.net.URL

@Service
class DocumentHandler(
    val senderService: SenderService,
    val botConfig: BotConfig,
) {

    fun hande(document: Document) {
        val getFile = GetFile(document.fileId)
        val name = document.fileName
        val url = URI(senderService.downLoadTgFile(getFile).getFileUrl(botConfig.token)).toURL()
    }
}