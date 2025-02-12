package org.superapp.negotiatorbot.webclient.port

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.superapp.negotiatorbot.webclient.dto.dadata.DadataCompanySearchResult
import org.superapp.negotiatorbot.webclient.dto.dadata.DadataRequest


@FeignClient(value = "dadataport", url = "\${dadata.url}")
interface DadataPort {

    @PostMapping("/findById/party")
    fun findCompanyByInn(
        @RequestBody request: DadataRequest,
        @RequestHeader("Authorization") token: String
    ): DadataCompanySearchResult
}