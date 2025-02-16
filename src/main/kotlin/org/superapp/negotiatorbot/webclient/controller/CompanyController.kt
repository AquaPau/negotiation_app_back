package org.superapp.negotiatorbot.webclient.controller

import kotlinx.coroutines.launch
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.config.AppCoroutineScope
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.CounterpartyDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import org.superapp.negotiatorbot.webclient.service.company.CompanyService
import org.superapp.negotiatorbot.webclient.service.util.MultipartFileValidator

@RestController
@RequestMapping("/api/company/own")
class CompanyController(
    private val companyService: CompanyService,
    private val appCoroutineScope: AppCoroutineScope,
    private val multipartFileValidator: MultipartFileValidator
) {

    @GetMapping()
    fun getCompanyProfile(): CompanyProfileDto {
        return companyService.getOwnCompany()
    }

    @PostMapping()
    fun createNewCompanyProfile(@RequestBody profile: NewCompanyProfile): CompanyProfileDto {
        return companyService.createCompany(profile)
    }

    @PutMapping("/{companyId}/document")
    fun uploadOwnCompanyDocuments(
        @RequestParam("documents") files: List<MultipartFile>,
        @RequestParam("fileNamesWithExtensions") fileNamesWithExtensions: List<String>,
        @RequestParam("types") types: List<DocumentType>,
        @PathVariable("companyId") companyId: Long
    ) {
        val fileContents = files.mapIndexed { index, file ->
            RawDocumentAndMetatype(
                types[index],
                file.bytes,
                fileNamesWithExtensions[index]
            )
        }
        multipartFileValidator.validate(files, fileNamesWithExtensions)
        appCoroutineScope.launch { companyService.uploadDocuments(fileContents, companyId, BusinessType.USER) }
    }

    @GetMapping("/own/{companyId}/counterparties")
    fun getCounterPartiesByCompanyId(@PathVariable companyId: Long): List<CounterpartyDto> {
        return companyService.getCounterparties(companyId)
    }

    @GetMapping("/{companyId}/document/{documentId}/info")
    fun getFileInfoById(@PathVariable documentId: Int, @PathVariable companyId: Int) {

    }

    @DeleteMapping("/{companyId}/document/{documentId}")
    fun deleteFileById(@PathVariable documentId: Int, @PathVariable companyId: Int) {

    }


    @ExceptionHandler(IllegalArgumentException::class)
    fun handleException(e: IllegalArgumentException): ResponseEntity<String?> =
        ResponseEntity.badRequest().body(e.message)

}