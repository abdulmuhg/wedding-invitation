package com.mrc.wedding.routes

import com.mrc.wedding.repositories.WishReactionRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@kotlinx.serialization.Serializable
data class ReactionRequest(
    val guestName: String,
    val reactionType: String
)

fun Route.wishReactionRoutes() {
    val repository = WishReactionRepository()

    route("/api/weddings/{weddingId}/wishes/{wishId}/reactions") {
        post {
            try {
                val wishId = call.parameters["wishId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid wish ID")

                val request = call.receive<ReactionRequest>()

                // Validate guest name
                if (request.guestName.isBlank() || request.guestName.length > 255) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        "Guest name must be between 1 and 255 characters"
                    )
                }

                // Validate reaction type
                if (!VALID_REACTIONS.contains(request.reactionType)) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        "Invalid reaction type. Must be one of: ${VALID_REACTIONS.joinToString()}"
                    )
                }

                // Add reaction with validation
                val result = repository.addReaction(
                    wishId = wishId,
                    guestName = request.guestName,
                    reactionType = request.reactionType
                )
                if (result.isSuccess) {
                    call.respond(
                        HttpStatusCode.Created,
                        mapOf("id" to result.getOrNull())
                    )
                } else {
                    call.respond(
                        HttpStatusCode.TooManyRequests,
                        mapOf("error" to (result.exceptionOrNull()?.message ?: "Rate limit exceeded"))
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to (e.message ?: "Unknown error occurred"))
                )
            }
        }
    }
}

private val VALID_REACTIONS = setOf("LIKE", "LOVE", "LAUGH", "WOW", "SAD", "ANGRY")