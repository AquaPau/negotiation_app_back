package org.superapp.negotiatorbot.webclient.dto.dadata

data class DadataCompanySearchResult(
    val suggestions: List<DadataCompany>
)

data class DadataCompany(
    val value: String,
    val data: CompanyParams
)

data class CompanyParams(
    val inn: String,
    val ogrn: String,
    val management: CompanyManagement?,
    val address: CompanyAddress
)

data class CompanyManagement(
    val name: String,
    val post: String
)
data class CompanyAddress(
    val value: String
)


