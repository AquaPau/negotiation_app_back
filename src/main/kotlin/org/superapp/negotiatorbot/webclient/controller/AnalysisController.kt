package org.superapp.negotiatorbot.webclient.controller

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.service.functiona.AnalyseService

@RestController
@RequestMapping("/analyse")
class AnalysisController(
    private val analyseService: AnalyseService
) {

    @PutMapping("/prompt")
    fun analyseUsingPrompt(@RequestBody data: MultipartFile) {
    }

    @PutMapping("/document/{documentId}/risks")
    fun analyseRisks(@PathVariable documentId: Long) {

    }

    @PutMapping("/counterparty/{counterPartyId}/opportunities")
    fun analyseOpportunities(@PathVariable counterPartyId: Long) {

    }
}