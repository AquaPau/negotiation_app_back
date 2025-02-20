package org.superapp.negotiatorbot.webclient.controller

import org.springframework.web.bind.annotation.*
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.CounterpartyDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.service.company.CompanyService

@RestController
@RequestMapping("/api/company")
class ContractorController(
    private val companyService: CompanyService
) {

    @PostMapping("/{companyId}/contractor")
    fun createNewCompanyProfile(
        @PathVariable companyId: Long,
        @RequestBody profile: NewCompanyProfile
    ): CompanyProfileDto {
        return companyService.createCompany(companyId, profile, isOwn = false)
    }

    @GetMapping("/{companyId}/contractor")
    fun getCounterPartiesByCompanyId(@PathVariable companyId: Long): List<CounterpartyDto> {
        return companyService.getCounterparties(companyId)
    }

    @GetMapping("/{companyId}/contractor/{contractorId}")
    fun getContractor(@PathVariable companyId: Long, @PathVariable contractorId: Long): CompanyProfileDto {
        return companyService.getContractor(companyId, contractorId)
    }


}