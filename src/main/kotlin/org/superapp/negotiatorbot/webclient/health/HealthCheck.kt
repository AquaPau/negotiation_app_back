package org.superapp.negotiatorbot.webclient.health

import io.awspring.cloud.s3.S3Template
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class HealthCheck(private val s3Template: S3Template) : HealthIndicator {

    @Value("\${spring.cloud.aws.s3.bucket-name}")
    var bucketName: String? = null

    override fun health(): Health {
        try {
            if (s3Template.bucketExists(bucketName)) {
                log.debug("Bucket $bucketName is reachable.")
            } else {
                log.warn("No bucket configured for ${bucketName}")
            }
        } catch (e: Exception) {
            log.warn("Error checking s3 bucket {}", bucketName, e)
        }
        return Health.up().build()
    }
}