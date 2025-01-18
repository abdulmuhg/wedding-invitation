package com.mrc.wedding.routes

import com.mrc.wedding.repositories.WishRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@kotlinx.serialization.Serializable
data class CreateWishRequest(
    val guestName: String,
    val message: String
)

fun Route.wishRoutes() {
    val repository = WishRepository()

    route("/api/weddings/{weddingId}/wishes") {
        get {
            val weddingId = call.parameters["weddingId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid wedding ID")

            val wishes = repository.getWishesByWeddingId(weddingId)
            call.respond(wishes)
        }

        post {
            val weddingId = call.parameters["weddingId"]?.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid wedding ID")

            val request = call.receive<CreateWishRequest>()
            val wishId = repository.createWish(
                weddingId = weddingId,
                guestName = request.guestName,
                message = request.message
            )

            call.respond(HttpStatusCode.Created, mapOf("id" to wishId))
        }
    }
}