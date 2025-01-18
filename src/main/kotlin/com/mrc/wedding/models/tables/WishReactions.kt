package com.mrc.wedding.models.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object WishReactions : Table() {
    val id = integer("id").autoIncrement()
    val wishId = integer("wish_id").references(WeddingWishes.id)
    val guestName = varchar("guest_name", 255)  // Who reacted
    val reactionType = varchar("reaction_type", 50)  // e.g., "LIKE", "LOVE", "LAUGH"
    val createdAt = datetime("created_at")
    
    override val primaryKey = PrimaryKey(id)
    
    // Ensure one reaction per guest per wish
    init {
        uniqueIndex("unique_wish_guest_reaction", wishId, guestName)
    }
}