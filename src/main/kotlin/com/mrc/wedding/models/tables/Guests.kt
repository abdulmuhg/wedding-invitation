package com.mrc.wedding.models.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Guests : Table() {
    val id = integer("id").autoIncrement()
    val weddingId = integer("wedding_id").references(Weddings.id)
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val invitationCode = varchar("invitation_code", 50).uniqueIndex()
    val numberOfGuests = integer("number_of_guests").default(1)
    val isAttending = bool("is_attending").nullable()
    val message = text("message").nullable()
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}