package com.mrc.wedding.repositories

import com.mrc.wedding.models.tables.Guests
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class GuestRepository {
    // Create new guest
    fun createGuest(name: String, email: String, invitationCode: String): Int {
        return transaction {
            Guests.insert {
                it[Guests.name] = name
                it[Guests.email] = email
                it[Guests.invitationCode] = invitationCode
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }[Guests.id]
        }
    }

    // Get all guests
    fun getAllGuests() = transaction {
        Guests.selectAll().map { row ->
            GuestDTO(
                id = row[Guests.id],
                name = row[Guests.name],
                email = row[Guests.email],
                invitationCode = row[Guests.invitationCode],
                numberOfGuests = row[Guests.numberOfGuests],
                isAttending = row[Guests.isAttending],
                message = row[Guests.message]
            )
        }
    }

    // Get guest by invitation code
    fun findByInvitationCode(code: String) = transaction {
        Guests.select { Guests.invitationCode eq code }
            .map { row ->
                GuestDTO(
                    id = row[Guests.id],
                    name = row[Guests.name],
                    email = row[Guests.email],
                    invitationCode = row[Guests.invitationCode],
                    numberOfGuests = row[Guests.numberOfGuests],
                    isAttending = row[Guests.isAttending],
                    message = row[Guests.message]
                )
            }.singleOrNull()
    }

    // Update RSVP status
    fun updateRSVP(code: String, isAttending: Boolean, message: String?) = transaction {
        Guests.update({ Guests.invitationCode eq code }) {
            it[Guests.isAttending] = isAttending
            it[Guests.message] = message
            it[updatedAt] = LocalDateTime.now()
        }
    }
}

// Data Transfer Object
@kotlinx.serialization.Serializable
data class GuestDTO(
    val id: Int,
    val name: String,
    val email: String,
    val invitationCode: String,
    val numberOfGuests: Int = 1,
    val isAttending: Boolean? = null,
    val message: String? = null
)