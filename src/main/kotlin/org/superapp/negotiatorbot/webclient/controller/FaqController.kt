package org.superapp.negotiatorbot.webclient.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.superapp.negotiatorbot.webclient.service.FaqService

@RestController
@RequestMapping("/api/faq")
class FaqController(private val faqService: FaqService) {

    @GetMapping
    fun getFaq(): String {
        return faqService.get()
    }
}