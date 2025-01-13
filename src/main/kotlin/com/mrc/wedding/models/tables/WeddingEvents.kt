package com.mrc.wedding.models.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object WeddingEvents : Table() {
    val id = integer("id").autoIncrement()
    val weddingId = integer("wedding_id").references(Weddings.id)
    val name = varchar("name", 100)  // "Holy Matrimony", "Reception"
    val eventDate = datetime("event_date")
    val location = varchar("location", 255)
    val mapLink = varchar("map_link", 500).nullable()
    val description = text("description").nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}