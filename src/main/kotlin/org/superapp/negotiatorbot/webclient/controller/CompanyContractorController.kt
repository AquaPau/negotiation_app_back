package org.superapp.negotiatorbot.webclient.controller

import org.springframework.web.bind.annotation.*
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.CounterpartyDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.service.contractor.ContractorCrudService

@RestController
@RequestMapping("/api/company")
class CompanyContractorController(
    private val contractorCrudService: ContractorCrudService
) {

    @PostMapping("/{companyId}/contractor")
    fun createNewCompanyProfile(
        @PathVariable companyId: Long,
        @RequestBody profile: NewCompanyProfile
    ): CompanyProfileDto {
        return contractorCrudService.create(companyId, profile)
    }

    @GetMapping("/{companyId}/contractor")
    fun getCounterPartiesByCompanyId(@PathVariable companyId: Long): List<CounterpartyDto> {
        return contractorCrudService.getContractorsByCompanyId(companyId)
    }

    @GetMapping("/{companyId}/contractor/{contractorId}")
    fun getContractor(@PathVariable companyId: Long, @PathVariable contractorId: Long): CompanyProfileDto {
        return contractorCrudService.get(companyId, contractorId)
    }

    @DeleteMapping("/{contractorId}/contractor")
    fun deleteContractor(@PathVariable contractorId: Long) {
        contractorCrudService.delete(contractorId)
    }

}