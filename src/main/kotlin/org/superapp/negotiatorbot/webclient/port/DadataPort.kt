package org.superapp.negotiatorbot.webclient.port

import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.dadata.DadataCompanySearchResult

interface DadataPort {

    fun getCompanyByInn(inn: Long): DadataCompanySearchResult
}

@Service
class DadataPortImpl(): DadataPort {
    override fun getCompanyByInn(inn: Long): DadataCompanySearchResult {
        TODO("Not yet implemented")
    }

}