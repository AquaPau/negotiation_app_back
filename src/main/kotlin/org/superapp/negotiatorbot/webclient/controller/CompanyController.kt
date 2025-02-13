package org.superapp.negotiatorbot.webclient.controller

import kotlinx.coroutines.launch
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.config.AppCoroutineScope
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.dto.company.UpdatedCompanyProfile
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.enum.DocumentType
import org.superapp.negotiatorbot.webclient.service.company.CompanyService

@RestController
@RequestMapping("/company")
class CompanyController(
    private val companyService: CompanyService,
    private val appCoroutineScope: AppCoroutineScope,
) {

    @PostMapping
    fun createNewCompanyProfile(@RequestBody profile: NewCompanyProfile): CompanyProfileDto {
        return companyService.createCompany(profile)
    }

    @PutMapping
    fun updateCompanyProfileById(@RequestBody updatedProfile: UpdatedCompanyProfile) {

    }

    @PutMapping("/own/{companyId}/document")
    fun uploadOwnCompanyDocuments(
        @RequestParam("documents") files: List<MultipartFile>,
        @RequestParam("types") types: List<DocumentType>,
        @PathVariable("companyId") companyId: Long
    ) {
        appCoroutineScope.launch { companyService.uploadDocuments(files, types, companyId, BusinessType.USER) }
    }

    @PutMapping("/counterpartu/{companyId}/document")
    fun uploadCounterpartyDocuments(
        @RequestParam("documents") files: List<MultipartFile>,
        @RequestParam("types") types: List<DocumentType>,
        @PathVariable("companyId") companyId: Long
    ) {
        appCoroutineScope.launch { companyService.uploadDocuments(files, types, companyId, BusinessType.PARTNER) }
    }

    @GetMapping("/{companyId}/document/{documentId}/info")
    fun getFileInfoById(@PathVariable documentId: Int, @PathVariable companyId: Int) {

    }

    @DeleteMapping("/{companyId}/document/{documentId}")
    fun deleteFileById(@PathVariable documentId: Int, @PathVariable companyId: Int) {

    }
}