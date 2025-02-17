package org.superapp.negotiatorbot.webclient.controller

import kotlinx.coroutines.launch
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.config.AppCoroutineScope
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.dto.company.UpdatedCompanyProfile
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import org.superapp.negotiatorbot.webclient.service.company.CompanyService
import org.superapp.negotiatorbot.webclient.service.serversidefile.DocumentService
import org.superapp.negotiatorbot.webclient.service.util.MultipartFileValidator

@RestController
@RequestMapping("api/company")
class ContractorController(
    private val companyService: CompanyService,
    private val documentService: DocumentService,
    private val appCoroutineScope: AppCoroutineScope,
    private val multipartFileValidator: MultipartFileValidator
) {

    @PostMapping
    fun createNewCompanyProfile(@RequestBody profile: NewCompanyProfile): CompanyProfileDto {
        return companyService.createCompany(profile)
    }

    @PutMapping
    fun updateCompanyProfileById(@RequestBody updatedProfile: UpdatedCompanyProfile) {

    }

    @PutMapping("/counterparty/{counterpartyId}/document")
    fun uploadCounterpartyDocuments(
        @RequestParam("documents") files: List<MultipartFile>,
        @RequestParam("types") types: List<DocumentType>,
        @PathVariable("counterpartyId") counterpartyId: Long
    ) {
        val fileNamesWithExtensions = files.map {
            it.originalFilename!!
        }
        val fileContents = files.mapIndexed { index, file ->
            RawDocumentAndMetatype(
                types[index],
                fileNamesWithExtensions[index]
            ).apply { fileContent = file.bytes }
        }
        multipartFileValidator.validate(files, fileNamesWithExtensions)
        appCoroutineScope.launch { companyService.uploadDocuments(fileContents, counterpartyId, BusinessType.PARTNER) }
    }

    @GetMapping("/counterparty/{counterpartyId}/documents/info")
    fun getDocumentsInfo(@PathVariable counterpartyId: Long): List<DocumentMetadataDto> {
        return documentService.getMetadataByCounterPartyId(counterpartyId)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleException(e: IllegalArgumentException): ResponseEntity<String?> =
        ResponseEntity.badRequest().body(e.message)
}