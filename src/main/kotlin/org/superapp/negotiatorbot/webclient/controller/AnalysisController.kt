package org.superapp.negotiatorbot.webclient.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.superapp.negotiatorbot.webclient.service.functiona.AnalyseService

@RestController
@RequestMapping("/api/analyse")
class AnalysisController(
    private val analyseService: AnalyseService
) {
    @GetMapping("/document/{documentId}/risks")
    fun analyseRisks(@PathVariable documentId: Long) {
        analyseService.detectRisks(documentId)
    }

    @GetMapping("/document/{documentId}/risks/retry")
    fun analyseRisksRetry(@PathVariable documentId: Long) {
        analyseService.updateThreadAndRun(documentId) { analyseService.detectRisks(it) }
    }

    @GetMapping("/document/{documentId}/description")
    fun analyseDoc(@PathVariable documentId: Long) {
        analyseService.provideDescription(documentId)
    }

    @GetMapping("/document/{documentId}/description/retry")
    fun analyseDocRetry(@PathVariable documentId: Long) {
        analyseService.updateThreadAndRun(documentId) { analyseService.provideDescription(it) }
    }

    @GetMapping("/company/{companyId}/contractor/{contractorId}/opportunities")
    fun analyseOpportunities(@PathVariable contractorId: Long) {

    }

}