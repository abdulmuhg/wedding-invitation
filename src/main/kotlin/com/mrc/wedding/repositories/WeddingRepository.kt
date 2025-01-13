package com.mrc.wedding.repositories

import com.mrc.wedding.models.dto.EventDTO
import com.mrc.wedding.models.dto.GiftDTO
import com.mrc.wedding.models.dto.WeddingDTO
import com.mrc.wedding.models.tables.WeddingEvents
import com.mrc.wedding.models.tables.WeddingGifts
import com.mrc.wedding.models.tables.Weddings
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class WeddingRepository {
    fun createWedding(
        brideFirstName: String,
        brideLastName: String,
        groomFirstName: String,
        groomLastName: String,
        eventDate: LocalDateTime,
        slug: String
    ): Int = transaction {
        Weddings.insert {
            it[this.brideFirstName] = brideFirstName
            it[this.brideLastName] = brideLastName
            it[this.groomFirstName] = groomFirstName
            it[this.groomLastName] = groomLastName
            it[this.eventDate] = eventDate
            it[this.slug] = slug
            it[createdAt] = LocalDateTime.now()
            it[updatedAt] = LocalDateTime.now()
        }[Weddings.id]
    }

    fun getWeddingBySlug(slug: String): WeddingDTO? = transaction {
        val wedding = Weddings
            .select { Weddings.slug eq slug }
            .singleOrNull() ?: return@transaction null

        val events = WeddingEvents
            .select { WeddingEvents.weddingId eq wedding[Weddings.id] }
            .map { 
                EventDTO(
                    id = it[WeddingEvents.id],
                    name = it[WeddingEvents.name],
                    eventDate = it[WeddingEvents.eventDate].toString(),
                    location = it[WeddingEvents.location],
                    mapLink = it[WeddingEvents.mapLink],
                    description = it[WeddingEvents.description]
                )
            }

        val gifts = WeddingGifts
            .select { WeddingGifts.weddingId eq wedding[Weddings.id] }
            .map {
                GiftDTO(
                    bankName = it[WeddingGifts.bankName],
                    accountNumber = it[WeddingGifts.accountNumber],
                    accountHolder = it[WeddingGifts.accountHolder],
                    description = it[WeddingGifts.description]
                )
            }

        WeddingDTO(
            id = wedding[Weddings.id],
            slug = wedding[Weddings.slug],
            brideFullName = "${wedding[Weddings.brideFirstName]} ${wedding[Weddings.brideLastName]}",
            groomFullName = "${wedding[Weddings.groomFirstName]} ${wedding[Weddings.groomLastName]}",
            eventDate = wedding[Weddings.eventDate].toString(),
            events = events,
            gifts = gifts
        )
    }
}