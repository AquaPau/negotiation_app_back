package org.superapp.negotiatorbot.webclient.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.service.company.CompanyCrudService

@RestController
@RequestMapping("/api/company")
class CompanyController(
    private val companyCrudService: CompanyCrudService
) {

    val log = KotlinLogging.logger {}
    @GetMapping()
    fun getCompanies(): List<CompanyProfileDto> {
        return companyCrudService.getAll()
    }

    @GetMapping("/{companyId}")
    fun getCompany(@PathVariable companyId: Long): CompanyProfileDto {
        return companyCrudService.get(companyId)
    }

    @PostMapping()
    fun createNewCompanyProfile(@RequestBody profile: NewCompanyProfile): CompanyProfileDto {
        return companyCrudService.create(null, profile)
    }

    @DeleteMapping("/{companyId}")
    fun deleteCompany(@PathVariable companyId: Long) {
        companyCrudService.delete(companyId)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleException(e: IllegalArgumentException): ResponseEntity<String?> {
        log.error(e.message, e)
        return ResponseEntity.badRequest().body("Возникла непредвиденная ошибка сервера")
    }

}