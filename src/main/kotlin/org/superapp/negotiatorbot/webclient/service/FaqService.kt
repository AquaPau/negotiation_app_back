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
        if (file.exists()) {
            return file.readText()
        } else {
            val inputStream = javaClass.classLoader.getResourceAsStream("README.md")
                ?: return "FAQ не найдено"
            return inputStream.bufferedReader().use { it.readText() }
        }
    }

}