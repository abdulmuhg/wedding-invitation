package com.mrc.wedding.services

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import java.util.UUID
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider

class S3Service(
    private val bucketName: String,
    private val region: String,
    private val accessKeyId: String,
    private val secretAccessKey: String
) {
    private val credentialsProvider = StaticCredentialsProvider {
        accessKeyId = this@S3Service.accessKeyId
        secretAccessKey = this@S3Service.secretAccessKey
    }

    suspend fun uploadPhoto(bytes: ByteArray, contentType: String): Result<String> {
        return try {
            val key = "photos/${UUID.randomUUID()}"

            S3Client.fromEnvironment {
                region = this@S3Service.region
                credentialsProvider = this@S3Service.credentialsProvider
            }.use { s3 ->
                val request = PutObjectRequest {
                    bucket = bucketName
                    this.key = key
                    body = ByteStream.fromBytes(bytes)
                    this.contentType = contentType
                }
                s3.putObject(request)
            }

            Result.success("https://$bucketName.s3.$region.amazonaws.com/$key")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Configuration class to handle AWS settings
 */
data class AWSConfig(
    val bucketName: String,
    val region: String,
    val accessKeyId: String,
    val secretAccessKey: String
) {
    companion object {
        fun fromEnv(): AWSConfig {
            return AWSConfig(
                bucketName = System.getenv("AWS_BUCKET_NAME") ?: throw IllegalStateException("AWS_BUCKET_NAME not set"),
                region = System.getenv("AWS_REGION") ?: throw IllegalStateException("AWS_REGION not set"),
                accessKeyId = System.getenv("AWS_ACCESS_KEY_ID") ?: throw IllegalStateException("AWS_ACCESS_KEY_ID not set"),
                secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY") ?: throw IllegalStateException("AWS_SECRET_ACCESS_KEY not set")
            )
        }
    }
}