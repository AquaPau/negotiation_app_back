package org.superapp.negotiatorbot.webclient.controller

import org.springframework.web.bind.annotation.*
import org.superapp.negotiatorbot.webclient.enums.LegalType
import org.superapp.negotiatorbot.webclient.service.functionality.AnalyseService

@RestController
@RequestMapping("/api/analyse")
class AnalysisController(
    private val analyseService: AnalyseService
) {
    @GetMapping("/document/{documentId}/risks")
    fun getDocumentRisks(@PathVariable documentId: Long, @RequestParam(required = false) retry: Boolean = false) {
        if (retry) {
            analyseService.updateThreadAndRun(documentId) { analyseService.detectRisks(it, LegalType.ENTERPRISE) }
        } else {
            analyseService.detectRisks(documentId, LegalType.ENTERPRISE)
        }
    }

    @GetMapping("/document/{documentId}/description")
    fun getDocumentDescription(@PathVariable documentId: Long, @RequestParam(required = false) retry: Boolean = false) {
        if (retry) {
            analyseService.updateThreadAndRun(documentId) {
                analyseService.provideDescription(
                    it,
                    LegalType.ENTERPRISE
                )
            }
        } else {
            analyseService.provideDescription(documentId, LegalType.ENTERPRISE)
        }
    }

    @GetMapping("project/{projectId}/resolution")
    fun getProjectResolution(@PathVariable projectId: Long, @RequestParam retry: Boolean) {
        if (retry) {
            analyseService.updateThreadAndRun(projectId) {
                analyseService.provideProjectResolution(
                    it
                )
            }
        } else {
            analyseService.provideProjectResolution(projectId)
        }
    }

}