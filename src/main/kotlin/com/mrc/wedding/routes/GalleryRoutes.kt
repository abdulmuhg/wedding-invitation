package com.mrc.wedding.routes

import com.mrc.wedding.repositories.GalleryAlbumRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@kotlinx.serialization.Serializable
data class CreateAlbumRequest(
    val title: String,
    val description: String?,
    val coverPhotoUrl: String,
    val orderIndex: Int
)

@kotlinx.serialization.Serializable
data class UpdateAlbumRequest(
    val title: String?,
    val description: String?,
    val coverPhotoUrl: String?,
    val orderIndex: Int?
)

fun Route.galleryRoutes() {
    val repository = GalleryAlbumRepository()

    route("/api/weddings/{weddingId}/gallery/albums") {
        get {
            val weddingId = call.parameters["weddingId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid wedding ID")

            val albums = repository.getAlbumsByWeddingId(weddingId)
            call.respond(albums)
        }

        post {
            val weddingId = call.parameters["weddingId"]?.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid wedding ID")

            val request = call.receive<CreateAlbumRequest>()
            val albumId = repository.createAlbum(
                weddingId = weddingId,
                title = request.title,
                description = request.description,
                coverPhotoUrl = request.coverPhotoUrl,
                orderIndex = request.orderIndex
            )

            call.respond(HttpStatusCode.Created, mapOf("id" to albumId))
        }

        put("/{albumId}") {
            val albumId = call.parameters["albumId"]?.toIntOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid album ID")

            val request = call.receive<UpdateAlbumRequest>()
            val updated = repository.updateAlbum(
                albumId = albumId,
                title = request.title,
                description = request.description,
                coverPhotoUrl = request.coverPhotoUrl,
                orderIndex = request.orderIndex
            )

            if (updated) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound, "Album not found")
            }
        }

        delete("/{albumId}") {
            val albumId = call.parameters["albumId"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid album ID")

            val deleted = repository.deleteAlbum(albumId)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Album not found")
            }
        }
    }
}