package com.mrc.wedding.routes

import com.mrc.wedding.repositories.PhotoRepository
import com.mrc.wedding.services.AWSConfig
import com.mrc.wedding.services.S3Service
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@kotlinx.serialization.Serializable
data class PhotoUploadResponse(
    val id: Int,
    val url: String
)

@kotlinx.serialization.Serializable
data class ErrorResponse(
    val message: String
)

fun Route.photoRoutes(awsConfig: AWSConfig) {
    val s3Service = S3Service(
        bucketName = awsConfig.bucketName,
        region = awsConfig.region,
        accessKeyId = awsConfig.accessKeyId,
        secretAccessKey = awsConfig.secretAccessKey
    )
    val repository = PhotoRepository()

    route("/api/photos") {
        post {
            try {
                val multipart = call.receiveMultipart()
                var title = ""
                var description: String? = null
                var category = "PRE_WEDDING"
                var photoBytes: ByteArray? = null
                var contentType = ""

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            when (part.name) {
                                "title" -> title = part.value
                                "description" -> description = part.value
                                "category" -> category = part.value
                            }
                        }
                        is PartData.FileItem -> {
                            photoBytes = part.streamProvider().readBytes()
                            contentType = part.contentType?.toString() ?: "image/jpeg"
                        }
                        else -> {}
                    }
                    part.dispose()
                }

                if (photoBytes != null) {
                    val result = s3Service.uploadPhoto(photoBytes!!, contentType)

                    if (result.isSuccess) {
                        val url = result.getOrNull()!!
                        val photoId = repository.addPhoto(title, description, url, category)
                        call.respond(PhotoUploadResponse(id = photoId, url = url))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to upload photo: ${result.exceptionOrNull()?.message}")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(message = "No photo provided"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error processing request: ${e.message}")
            }
        }

        get("/{category}") {
            val category = call.parameters["category"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(message = "Category is required")
            )
            val photos = repository.getPhotosByCategory(category)
            call.respond(photos)
        }
    }
}