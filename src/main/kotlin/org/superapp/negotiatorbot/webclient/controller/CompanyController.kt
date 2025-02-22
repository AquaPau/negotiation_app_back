package org.superapp.negotiatorbot.webclient.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.service.company.CompanyService

@RestController
@RequestMapping("/api/company")
class CompanyController(
    private val companyService: CompanyService,
) {

    @GetMapping()
    fun getCompanies(): List<CompanyProfileDto> {
        return companyService.getCompanies()
    }

    @GetMapping("/{companyId}")
    fun getCompany(@PathVariable companyId: Long): CompanyProfileDto {
        return companyService.getCompanyById(companyId)
    }

    @PostMapping()
    fun createNewCompanyProfile(@RequestBody profile: NewCompanyProfile): CompanyProfileDto {
        return companyService.createCompany(null, profile, isOwn = true)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleException(e: IllegalArgumentException): ResponseEntity<String?> =
        ResponseEntity.badRequest().body(e.message)

}