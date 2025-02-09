package org.superapp.negotiatorbot.webclient.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class S3Config {
    @Value("\${spring.cloud.aws.s3.bucket-name}")
    var bucketName: String? = null
}