package org.superapp.negotiatorbot.webclient.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.dto.document.DocumentMetadataDto
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.service.project.ProjectDocumentService
import org.superapp.negotiatorbot.webclient.service.util.FileTransformationHelper
import org.superapp.negotiatorbot.webclient.service.util.MultipartFileValidator

@RestController
@RequestMapping("/api/project")
class ProjectDocumentController(
    private val projectDocumentService: ProjectDocumentService
) {
    @PutMapping("/{projectId}/document")
    fun uploadProjectDocuments(
        @RequestParam("documents") files: List<MultipartFile>,
        @RequestParam("types") types: List<DocumentType>,
        @PathVariable("projectId") projectId: Long
    ) {
        val (fileNamesWithExtensions, fileContents) = FileTransformationHelper.extractLoadedData(files, types)

        MultipartFileValidator.validate(files, fileNamesWithExtensions)
        runBlocking(Dispatchers.IO) {
            launch {
                projectDocumentService.uploadDocuments(
                    files = fileContents, relatedId = projectId, type = BusinessType.PROJECT
                )
            }
        }
    }

    @GetMapping("/{projectId}/document")
    fun getProjectDocuments(@PathVariable projectId: Long): List<DocumentMetadataDto> {
        return projectDocumentService.getDocuments(projectId, BusinessType.PROJECT)
    }

    @DeleteMapping("/{projectId}/document")
    fun deleteDocuments(@PathVariable projectId: Long) {
        projectDocumentService.deleteDocuments(projectId)
    }

    @DeleteMapping("/{projectId}/document/{documentId}")
    fun deleteFileById(@PathVariable projectId: Long, @PathVariable documentId: Long) {
        projectDocumentService.deleteDocumentById(documentId, projectId)
    }

}