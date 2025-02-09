package org.superapp.negotiatorbot.webclient.service.s3

import io.awspring.cloud.s3.S3Resource
import io.awspring.cloud.s3.S3Template
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.superapp.negotiatorbot.webclient.config.S3Config

private val log = KotlinLogging.logger {}

interface S3Service {
    fun upload(fullS3Location: String, multipartFile: MultipartFile)
    fun download(fullS3Location: String): S3Resource
}

@Service
class S3ServiceImpl(private val s3Template: S3Template, private val s3Config: S3Config) : S3Service {

    val bucketName = s3Config.bucketName

    override fun upload(fullS3Location: String, multipartFile: MultipartFile) {
        s3Template.upload(bucketName, fullS3Location, multipartFile.inputStream)
        log.info("File uploaded successfully to ${fullS3Location}")
    }

    override fun download(fullS3Location: String) = s3Template.download(bucketName, fullS3Location)
}