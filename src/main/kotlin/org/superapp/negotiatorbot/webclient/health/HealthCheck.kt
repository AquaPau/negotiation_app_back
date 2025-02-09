package org.superapp.negotiatorbot.webclient.health

import io.awspring.cloud.s3.S3Template
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.webclient.config.S3Config

private val log = KotlinLogging.logger {}

@Component
class HealthCheck(private val s3Template: S3Template, private val s3Config: S3Config) : HealthIndicator {


    override fun health(): Health {
        val bucketName = s3Config.bucketName ?: throw RuntimeException("Bucket name is null")
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