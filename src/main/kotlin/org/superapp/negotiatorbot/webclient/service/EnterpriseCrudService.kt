package org.superapp.negotiatorbot.webclient.service

import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile

interface EnterpriseCrudService<T> {
    fun create(enterpriseId: Long? = null, request: NewCompanyProfile): CompanyProfileDto
    fun delete(enterpriseId: Long)
}