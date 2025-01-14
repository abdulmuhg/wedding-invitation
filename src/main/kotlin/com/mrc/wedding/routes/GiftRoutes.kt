package com.mrc.wedding.routes

import com.mrc.wedding.repositories.WeddingGiftRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@kotlinx.serialization.Serializable
data class CreateGiftRequest(
    val bankName: String,
    val accountNumber: String,
    val accountHolder: String,
    val description: String? = null
)

@kotlinx.serialization.Serializable
data class UpdateGiftRequest(
    val bankName: String? = null,
    val accountNumber: String? = null,
    val accountHolder: String? = null,
    val description: String? = null
)

fun Route.giftRoutes() {
    val repository = WeddingGiftRepository()

    route("/api/weddings/{weddingId}/gifts") {
        // Get all gifts for a wedding
        get {
            val weddingId = call.parameters["weddingId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid wedding ID")

            val gifts = repository.getGiftsByWeddingId(weddingId)
            call.respond(gifts)
        }

        // Create a new gift
        post {
            val weddingId = call.parameters["weddingId"]?.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid wedding ID")

            val request = call.receive<CreateGiftRequest>()
            
            val giftId = repository.createGift(
                weddingId = weddingId,
                bankName = request.bankName,
                accountNumber = request.accountNumber,
                accountHolder = request.accountHolder,
                description = request.description
            )

            val gift = repository.getGiftById(giftId)
            call.respond(HttpStatusCode.Created, gift!!)
        }

        // Update a gift
        put("/{giftId}") {
            val giftId = call.parameters["giftId"]?.toIntOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid gift ID")

            val request = call.receive<UpdateGiftRequest>()
            
            val updated = repository.updateGift(
                giftId = giftId,
                bankName = request.bankName,
                accountNumber = request.accountNumber,
                accountHolder = request.accountHolder,
                description = request.description
            )

            if (updated) {
                val gift = repository.getGiftById(giftId)
                call.respond(gift!!)
            } else {
                call.respond(HttpStatusCode.NotFound, "Gift not found")
            }
        }

        // Delete a gift
        delete("/{giftId}") {
            val giftId = call.parameters["giftId"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid gift ID")

            val deleted = repository.deleteGift(giftId)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Gift not found")
            }
        }
    }
}