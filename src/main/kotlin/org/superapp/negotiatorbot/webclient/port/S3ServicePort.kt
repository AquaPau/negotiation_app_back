package org.superapp.negotiatorbot.webclient.port


import io.awspring.cloud.s3.S3Resource
import io.awspring.cloud.s3.S3Template
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.superapp.negotiatorbot.webclient.config.S3Config

private val log = KotlinLogging.logger {}

interface S3ServicePort {
    fun upload(fullS3Location: String, content: ByteArray)
    fun download(fullS3Location: String): S3Resource
    fun delete(fullS3Location: String)
}

@Service
class S3ServicePortImpl(private val s3Template: S3Template, s3Config: S3Config) : S3ServicePort {

    val bucketName = s3Config.bucketName

    override fun upload(fullS3Location: String, content: ByteArray) {
        s3Template.upload(bucketName!!, fullS3Location, content.inputStream())
        log.info("File uploaded successfully to $fullS3Location")
    }

    override fun download(fullS3Location: String): S3Resource = s3Template.download(bucketName!!, fullS3Location)

    override fun delete(fullS3Location: String) {
        s3Template.deleteObject(bucketName!!, fullS3Location)
        log.info("File deleted successfully or didnt present already in $fullS3Location")
    }
}