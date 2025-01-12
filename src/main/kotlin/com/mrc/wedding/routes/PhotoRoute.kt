package com.mrc.wedding.routes

import com.mrc.wedding.repositories.PhotoRepository
import com.mrc.wedding.services.S3Service
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.photoRoutes() {
    val s3Service = S3Service()
    val repository = PhotoRepository()

    route("/api/photos") {
        // Upload photo
        post {
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
                val url = s3Service.uploadPhoto(photoBytes!!, contentType)
                val photoId = repository.addPhoto(title, description, url, category)
                call.respond(mapOf("id" to photoId, "url" to url))
            } else {
                call.respond(HttpStatusCode.BadRequest, "No photo provided")
            }
        }

        // Get photos by category
        get("/{category}") {
            val category = call.parameters["category"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Category is required"
            )
            val photos = repository.getPhotosByCategory(category)
            call.respond(photos)
        }
    }
}