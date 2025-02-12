package org.superapp.negotiatorbot.webclient.controller

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.dto.company.NewCompanyProfile
import org.superapp.negotiatorbot.webclient.dto.company.UpdatedCompanyProfile

@RestController
@RequestMapping("/company")
class CompanyController {

    @PostMapping
    fun createNewCompanyProfile(@RequestBody profile: NewCompanyProfile) {

    }

    @PutMapping
    fun updateCompanyProfileById(@RequestBody updatedProfile: UpdatedCompanyProfile) {

    }

    @PutMapping("/{companyId}/document")
    fun uploadCompanyDocuments(@RequestBody data: MultipartFile) {

    }

    @GetMapping("/{companyId}/document/{documentId}/info")
    fun getFileInfoById(@PathVariable documentId: Int, @PathVariable companyId: Int) {

    }

    @DeleteMapping("/{companyId}/document/{documentId}")
    fun deleteFileById(@PathVariable documentId: Int, @PathVariable companyId: Int) {

    }
}