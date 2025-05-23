package org.superapp.negotiatorbot.webclient.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.dto.document.DocumentEnterpriseMetadataDto
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.service.company.CompanyDocumentService
import org.superapp.negotiatorbot.webclient.service.util.FileTransformationHelper
import org.superapp.negotiatorbot.webclient.service.util.MultipartFileValidator
import org.superapp.negotiatorbot.webclient.service.util.MultipartFileValidator.Companion.validate

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

        validate(files, fileNamesWithExtensions)
        runBlocking(Dispatchers.IO) {
            launch {
                companyDocumentService.uploadDocuments(
                    files = fileContents,
                    relatedId = companyId
                )
            }
        }
    }

    @GetMapping("/{companyId}/document")
    fun getCompanyDocuments(@PathVariable companyId: Long): List<DocumentEnterpriseMetadataDto> {
        return companyDocumentService.getDocuments(companyId, BusinessType.USER)
    }

    @GetMapping("/{companyId}/document/{documentId}")
    fun getCompanyDocument(@PathVariable companyId: Long, @PathVariable documentId: Long): DocumentEnterpriseMetadataDto {
        return companyDocumentService.getDocument(companyId, documentId)
    }

    @DeleteMapping("/{companyId}/document")
    fun deleteDocuments(@PathVariable companyId: Long) {
        companyDocumentService.deleteCompanyDocuments(companyId)
    }

    @DeleteMapping("{companyId}/document/{documentId}")
    fun deleteCompanyDocumentById(@PathVariable companyId: Long, @PathVariable documentId: Long) {
        companyDocumentService.deleteDocumentById(documentId, companyId)
    }

}