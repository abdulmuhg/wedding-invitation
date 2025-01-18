package com.mrc.wedding.models.tables

import org.jetbrains.exposed.sql.Table

object GalleryAlbums : Table() {
    val id = integer("id").autoIncrement()
    val weddingId = integer("wedding_id").references(Weddings.id)
    val title = varchar("title", 255)  // e.g. "Perfect Date", "Groom's Favorite"
    val description = text("description").nullable()
    val coverPhotoUrl = varchar("cover_photo_url", 500)
    val orderIndex = integer("order_index")  // For controlling display order
    
    override val primaryKey = PrimaryKey(id)
}