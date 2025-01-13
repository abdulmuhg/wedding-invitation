package com.mrc.wedding.models.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Photos : Table() {
    val id = integer("id").autoIncrement()
    val weddingId = integer("wedding_id").references(Weddings.id)
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val url = varchar("url", 500)
    val category = varchar("category", 50)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}