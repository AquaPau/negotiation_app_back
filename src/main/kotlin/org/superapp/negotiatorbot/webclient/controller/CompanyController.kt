package org.superapp.negotiatorbot.webclient.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.CounterpartyDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import org.superapp.negotiatorbot.webclient.service.company.CompanyService
import org.superapp.negotiatorbot.webclient.service.util.FileTransformationHelper
import org.superapp.negotiatorbot.webclient.service.util.MultipartFileValidator
import org.superapp.negotiatorbot.webclient.service.util.MultipartFileValidator.Companion.validate

@RestController
@RequestMapping("/api/company/own")
class CompanyController(
    private val companyService: CompanyService,
    private val multipartFileValidator: MultipartFileValidator
) {

    @GetMapping()
    fun getCompanyProfile(): CompanyProfileDto {
        return companyService.getOwnCompany()
    }

    @PostMapping()
    fun createNewCompanyProfile(@RequestBody profile: NewCompanyProfile): CompanyProfileDto {
        return companyService.createCompany(profile, isOwn = true)
    }

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
                companyService.uploadDocuments(
                    fileContents,
                    companyId,
                    BusinessType.USER
                )
            }
        }
    }

    @GetMapping("/{companyId}/counterparties")
    fun getCounterPartiesByCompanyId(@PathVariable companyId: Long): List<CounterpartyDto> {
        return companyService.getCounterparties(companyId)
    }

    @GetMapping("/{companyId}/documents")
    fun getCompanyDocuments(@PathVariable companyId: Long): List<DocumentMetadataDto> {
        return companyService.getDocuments(companyId, BusinessType.USER)
    }

    @DeleteMapping("/{companyId}/document/{documentId}")
    fun deleteFileById(@PathVariable documentId: Int, @PathVariable companyId: Int) {

    }


    @ExceptionHandler(IllegalArgumentException::class)
    fun handleException(e: IllegalArgumentException): ResponseEntity<String?> =
        ResponseEntity.badRequest().body(e.message)

}