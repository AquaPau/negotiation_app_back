package org.superapp.negotiatorbot.webclient.service.functiona

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.result.BeneficiaryDto
import org.superapp.negotiatorbot.webclient.dto.result.CompanyOpportunitiesDto
import org.superapp.negotiatorbot.webclient.dto.result.CompanyRisksDto


interface AnalyseService {

    fun detectBeneficiary(): BeneficiaryDto

    fun detectRisks(): CompanyRisksDto

    fun detectOpportunities(): CompanyOpportunitiesDto
}
@Service
class AnalyseServiceImpl(): AnalyseService {
    override fun detectBeneficiary(): BeneficiaryDto {
        TODO("Not yet implemented")
    }

    override fun detectRisks(): CompanyRisksDto {
        TODO("Not yet implemented")
    }

    override fun detectOpportunities(): CompanyOpportunitiesDto {
        TODO("Not yet implemented")
    }


}