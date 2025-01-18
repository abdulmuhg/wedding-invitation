package com.mrc.wedding.models.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object WeddingWishes : Table() {
    val id = integer("id").autoIncrement()
    val weddingId = integer("wedding_id").references(Weddings.id)
    val guestName = varchar("guest_name", 255)
    val message = text("message")
    val createdAt = datetime("created_at")
    
    override val primaryKey = PrimaryKey(id)
}