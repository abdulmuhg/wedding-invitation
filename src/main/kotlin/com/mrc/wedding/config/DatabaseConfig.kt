package com.mrc.wedding.config

import com.mrc.wedding.models.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConfig {
    fun init() {
        val databaseUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/wedding_db"
        val databaseUser = System.getenv("DATABASE_USER") ?: "wedding_user"
        val databasePassword = System.getenv("DATABASE_PASSWORD") ?: "wedding_password"

        Database.connect(
            url = databaseUrl,
            driver = "org.postgresql.Driver",
            user = databaseUser,
            password = databasePassword
        )

        // Create tables
        transaction {
            SchemaUtils.create(
                Weddings,
                WeddingEvents,
                WeddingGifts,
                Photos,
                Guests,
                WeddingWishes,
                WishReactions,
                GalleryAlbums,
            )
        }
    }
}