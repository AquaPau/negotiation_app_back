package org.superapp.negotiatorbot.webclient.dto.company

data class UpdatedCompanyProfile(
    val companyId: Int,
    val customUserName: String,
    //extra params user is able to provide to concrete the company without dadata scanning
    val inn: Long? = null,
    val ogrn: Long? = null
)
