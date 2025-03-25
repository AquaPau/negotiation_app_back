package org.superapp.negotiatorbot.webclient.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.service.contractor.ContractorDocumentService
import org.superapp.negotiatorbot.webclient.service.util.FileTransformationHelper
import org.superapp.negotiatorbot.webclient.service.util.MultipartFileValidator

@RestController
@RequestMapping("/api/company")
class CompanyContractorDocumentController(private val contractorDocumentService: ContractorDocumentService) {

    @DeleteMapping("{companyId}/contractor/{contractorId}/document/{documentId}")
    fun deleteContractorDocumentById(
        @PathVariable companyId: Long,
        @PathVariable contractorId: Long,
        @PathVariable documentId: Long
    ) {
        contractorDocumentService.deleteDocumentById(documentId, companyId, contractorId)
    }

    @GetMapping("/{companyId}/contractor/{contractorId}/document")
    fun getDocumentsInfo(@PathVariable companyId: Long, @PathVariable contractorId: Long): List<DocumentMetadataDto> {
        return contractorDocumentService.getDocuments(companyId, contractorId)
    }

    @PutMapping("/{companyId}/contractor/{contractorId}/document")
    fun uploadCounterpartyDocuments(
        @RequestParam("documents") files: List<MultipartFile>,
        @RequestParam("types") types: List<DocumentType>,
        @PathVariable("companyId") companyId: Long,
        @PathVariable("contractorId") contractorId: Long
    ) {
        val (fileNamesWithExtensions, fileContents) = FileTransformationHelper.extractLoadedData(files, types)
        MultipartFileValidator.validate(files, fileNamesWithExtensions)
        runBlocking(Dispatchers.IO) {
            launch {
                contractorDocumentService.uploadDocuments(
                    files = fileContents,
                    relatedId = contractorId
                )
            }
        }
    }

    @DeleteMapping("/{companyId}/contractor/{contractorId}/document")
    fun deleteDocuments(@PathVariable companyId: Long, @PathVariable contractorId: Long) {
        contractorDocumentService.deleteDocuments(contractorId)
    }
}