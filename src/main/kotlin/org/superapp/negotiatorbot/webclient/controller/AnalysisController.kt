package org.superapp.negotiatorbot.webclient.controller

import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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

    @PutMapping("/beneficiary")
    fun getBeneficiary(@RequestBody data: MultipartFile) {

    }

    @PutMapping("/risks")
    fun analyseRisks(@RequestBody data: MultipartFile) {

    }

    @PutMapping("/opportunities")
    fun analyseOpportunities(@RequestBody data: MultipartFile) {

    }
}