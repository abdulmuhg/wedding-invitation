package com.mrc.wedding.services

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import java.util.UUID

class S3Service {
    private val bucketName = "your-bucket-name"
    private val region = "your-region" // e.g., "ap-southeast-1"

    suspend fun uploadPhoto(bytes: ByteArray, contentType: String): String {
        val key = "photos/${UUID.randomUUID()}"

        S3Client.fromEnvironment {
            region = this@S3Service.region
        }.use { s3 ->
            val request = PutObjectRequest {
                bucket = bucketName
                this.key = key
                body = ByteStream.fromBytes(bytes)
                this.contentType = contentType
            }
            s3.putObject(request)
        }

        return "https://$bucketName.s3.$region.amazonaws.com/$key"
    }
}