package org.superapp.negotiatorbot.webclient.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import org.superapp.negotiatorbot.webclient.service.company.CompanyService
import org.superapp.negotiatorbot.webclient.service.serversidefile.DocumentService
import org.superapp.negotiatorbot.webclient.service.util.FileTransformationHelper.Companion.extractLoadedData
import org.superapp.negotiatorbot.webclient.service.util.MultipartFileValidator.Companion.validate

@RestController
@RequestMapping("api/company/counterparty")
class ContractorController(
    private val companyService: CompanyService,
    private val documentService: DocumentService
) {

    @PostMapping
    fun createNewCompanyProfile(@RequestBody profile: NewCompanyProfile): CompanyProfileDto {
        return companyService.createCompany(profile, isOwn = false)
    }

    @PutMapping("/{counterpartyId}/document")
    fun uploadCounterpartyDocuments(
        @RequestParam("documents") files: List<MultipartFile>,
        @RequestParam("types") types: List<DocumentType>,
        @PathVariable("counterpartyId") counterpartyId: Long
    ) {
        val (fileNamesWithExtensions, fileContents) = extractLoadedData(files, types)
        validate(files, fileNamesWithExtensions)
        runBlocking(Dispatchers.IO) {
            launch {
                companyService.uploadDocuments(
                    fileContents,
                    counterpartyId,
                    BusinessType.PARTNER
                )
            }
        }

        @GetMapping("/{counterpartyId}/documents")
        fun getDocumentsInfo(@PathVariable counterpartyId: Long): List<DocumentMetadataDto> {
            return documentService.getMetadataByCounterPartyId(counterpartyId)
        }
    }
}