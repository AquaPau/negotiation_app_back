package org.superapp.negotiatorbot.webclient.service.s3

import io.awspring.cloud.s3.S3Resource
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.dto.document.RawDocumentAndMetatype
import org.superapp.negotiatorbot.webclient.entity.DocumentMetadata
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.exception.TaskException
import org.superapp.negotiatorbot.webclient.port.S3ServicePort
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskRecordService

private val log = KotlinLogging.logger {}

interface S3Service {
    fun uploadToS3(document: DocumentMetadata, fileData: RawDocumentAndMetatype)
    fun download(document: DocumentMetadata, task: TaskRecord): S3Resource
    fun delete(document: DocumentMetadata)
}

@Service
class S3ServiceImpl(
    private val s3ServicePort: S3ServicePort,
    private val taskService: TaskRecordService,
) : S3Service {

    override fun uploadToS3(document: DocumentMetadata, fileData: RawDocumentAndMetatype) {
        s3ServicePort.upload(document.path!!, fileData.fileContent!!)
    }

    override fun download(document: DocumentMetadata, task: TaskRecord): S3Resource {
        try {
            val result = s3ServicePort.download(document.path!!)
            taskService.changeStatus(task, TaskStatus.DOWNLOADED_FROM_S3)
            return result
        } catch (e: Exception) {
            log.error("Error while downloading document [{}]", document.path, e)
            throw TaskException(TaskStatus.ERROR_DOWNLOAD_S3)
        }
    }

    override fun delete(document: DocumentMetadata) {
        s3ServicePort.delete(document.path!!)

    }
}