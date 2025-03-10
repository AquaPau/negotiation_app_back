package org.superapp.negotiatorbot.webclient.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.enum.BusinessType
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import org.superapp.negotiatorbot.webclient.service.company.CompanyDocumentService
import org.superapp.negotiatorbot.webclient.service.util.FileTransformationHelper
import org.superapp.negotiatorbot.webclient.service.util.MultipartFileValidator

@RestController
@RequestMapping("/api/company")
class CompanyDocumentController(
    private val companyDocumentService: CompanyDocumentService
) {
    @PutMapping("/{companyId}/document")
    fun uploadOwnCompanyDocuments(
        @RequestParam("documents") files: List<MultipartFile>,
        @RequestParam("types") types: List<DocumentType>,
        @PathVariable("companyId") companyId: Long
    ) {
        val (fileNamesWithExtensions, fileContents) = FileTransformationHelper.extractLoadedData(files, types)

        MultipartFileValidator.validate(files, fileNamesWithExtensions)
        runBlocking(Dispatchers.IO) {
            launch {
                companyDocumentService.uploadDocuments(
                    files = fileContents,
                    relatedId = companyId,
                    type = BusinessType.USER
                )
            }
        }
    }

    @GetMapping("/{companyId}/document")
    fun getCompanyDocuments(@PathVariable companyId: Long): List<DocumentMetadataDto> {
        return companyDocumentService.getDocuments(companyId, BusinessType.USER)
    }

    @DeleteMapping("/{companyId}/document")
    fun deleteDocuments(@PathVariable companyId: Long) {
        companyDocumentService.deleteCompanyDocuments(companyId)
    }

    @DeleteMapping("{companyId}/document/{documentId}")
    fun deleteCompanyDocumentById(@PathVariable companyId: Long, @PathVariable documentId: Long) {
        companyDocumentService.deleteDocumentById(documentId, companyId)
    }

    @DeleteMapping("{companyId}/contractor/{contractorId}/document/{documentId}")
    fun deleteContractorDocumentById(
        @PathVariable companyId: Long,
        @PathVariable contractorId: Long,
        @PathVariable documentId: Long
    ) {
        companyDocumentService.deleteDocumentById(documentId, companyId, contractorId)
    }

    @GetMapping("/{companyId}/contractor/{contractorId}/document")
    fun getDocumentsInfo(@PathVariable companyId: Long, @PathVariable contractorId: Long): List<DocumentMetadataDto> {
        return companyDocumentService.getMetadataByContractorId(companyId, contractorId)
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
                companyDocumentService.uploadDocuments(
                    files = fileContents,
                    relatedId = contractorId,
                    type = BusinessType.PARTNER
                )
            }
        }
    }

    @DeleteMapping("/{companyId}/contractor/{contractorId}/document")
    fun deleteDocuments(@PathVariable companyId: Long, @PathVariable contractorId: Long) {
        companyDocumentService.deleteContractorDocuments(contractorId)
    }

}