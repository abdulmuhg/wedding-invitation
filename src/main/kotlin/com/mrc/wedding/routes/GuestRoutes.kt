package com.mrc.wedding.routes

import com.mrc.wedding.repositories.GuestRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// Request DTOs
@kotlinx.serialization.Serializable
data class CreateGuestRequest(
    val name: String,
    val email: String,
    val numberOfGuests: Int = 1
)

@kotlinx.serialization.Serializable
data class RSVPRequest(
    val invitationCode: String,
    val isAttending: Boolean,
    val message: String? = null
)

fun Route.guestRoutes() {
    val repository = GuestRepository()

    route("/api/guests") {
        // Create new guest
        post {
            try {
                val request = call.receive<CreateGuestRequest>()
                val invitationCode = generateInvitationCode() // We'll implement this

                val guestId = repository.createGuest(
                    name = request.name,
                    email = request.email,
                    invitationCode = invitationCode
                )

                val guest = repository.findByInvitationCode(invitationCode)
                call.respond(HttpStatusCode.Created, guest!!)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Get all guests (admin endpoint)
        get {
            val guests = repository.getAllGuests()
            call.respond(guests)
        }

        // RSVP endpoint
        post("/rsvp") {
            try {
                val request = call.receive<RSVPRequest>()
                val guest = repository.findByInvitationCode(request.invitationCode)
                    ?: return@post call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Invalid invitation code")
                    )

                repository.updateRSVP(
                    code = request.invitationCode,
                    isAttending = request.isAttending,
                    message = request.message
                )

                call.respond(HttpStatusCode.OK, mapOf("message" to "RSVP updated successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Get guest by invitation code
        get("/{code}") {
            val code = call.parameters["code"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing invitation code")
            )

            val guest = repository.findByInvitationCode(code) ?: return@get call.respond(
                HttpStatusCode.NotFound,
                mapOf("error" to "Guest not found")
            )

            call.respond(guest)
        }
    }
}

// Helper function to generate unique invitation code
private fun generateInvitationCode(): String {
    return System.currentTimeMillis().toString(36).take(6).uppercase()
}