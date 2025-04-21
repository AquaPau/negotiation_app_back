package org.superapp.negotiatorbot.webclient.service

import org.springframework.stereotype.Service
import java.io.File

interface FaqService {

    fun get(): String
}

@Service
class FaqServiceImpl() : FaqService {
    override fun get(): String {
        val file = File("README.md")
        return if (file.exists()) file.readText() else "FAQ не найдено"
    }
}
