package org.superapp.negotiatorbot.webclient.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import org.superapp.negotiatorbot.webclient.service.company.CompanyService
import org.superapp.negotiatorbot.webclient.service.serversidefile.DocumentService
import org.superapp.negotiatorbot.webclient.service.util.FileTransformationHelper
import org.superapp.negotiatorbot.webclient.service.util.MultipartFileValidator

@RestController
@RequestMapping("/api/company")
class DocumentController(
    private val companyService: CompanyService,
    private val documentService: DocumentService
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
                companyService.uploadDocuments(
                    files = fileContents,
                    companyId = companyId,
                    contractorId = null,
                    type = BusinessType.USER
                )
            }
        }
    }

    @GetMapping("/{companyId}/document")
    fun getCompanyDocuments(@PathVariable companyId: Long): List<DocumentMetadataDto> {
        return companyService.getDocuments(companyId, BusinessType.USER)
    }

    @DeleteMapping("/{companyId}/document/{documentId}")
    fun deleteFileById(@PathVariable documentId: Int, @PathVariable companyId: Int) {

    }

    @GetMapping("/{companyId}/contractor/{contractorId}/document")
    fun getDocumentsInfo(@PathVariable companyId: Long, @PathVariable contractorId: Long): List<DocumentMetadataDto> {
        return documentService.getMetadataByCounterPartyId(companyId, contractorId)
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
                companyService.uploadDocuments(
                    files = fileContents,
                    companyId = companyId,
                    contractorId = contractorId,
                    type = BusinessType.PARTNER
                )
            }
        }
    }

}